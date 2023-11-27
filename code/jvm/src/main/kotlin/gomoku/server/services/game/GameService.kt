package gomoku.server.services.game

import gomoku.server.domain.game.Matchmaker
import gomoku.server.domain.game.errors.MoveError
import gomoku.server.domain.game.game.CellColor
import gomoku.server.domain.game.game.FinishedGame
import gomoku.server.domain.game.game.GameOutcome
import gomoku.server.domain.game.game.GameState
import gomoku.server.domain.game.game.OngoingGame
import gomoku.server.domain.game.game.move.Move
import gomoku.server.domain.game.game.move.Position
import gomoku.server.domain.game.game.toGameOutcome
import gomoku.server.domain.game.rules.Rules
import gomoku.server.domain.user.RankingUserData
import gomoku.server.domain.user.updateElo
import gomoku.server.http.controllers.game.models.CurrentTurnPlayerOutput
import gomoku.server.repository.Transaction
import gomoku.server.repository.TransactionManager
import gomoku.server.services.errors.game.CurrentTurnPlayerError
import gomoku.server.services.errors.game.ForfeitGameError
import gomoku.server.services.errors.game.GetGameError
import gomoku.server.services.errors.game.MakeMoveError
import gomoku.server.services.errors.game.MatchmakingError
import gomoku.utils.Failure
import gomoku.utils.Success
import gomoku.utils.failure
import gomoku.utils.success
import org.springframework.stereotype.Service
import kotlin.random.Random

/**
 * Service for game-related operations
 * @param transactionManager The transaction manager
 */
@Service
class GameService(private val transactionManager: TransactionManager) {

    /**
     * Starts the matchmaking process for the given rule and user.
     * @param ruleId id of the rule
     * @param userId the id of the user that wants to play
     * @return the result of the matchmaking process
     * @see MatchmakingResult
     */
    fun startMatchmakingProcess(ruleId: Int, userId: Int): MatchmakingResult {
        return transactionManager.run {
            val lobby = it.lobbyRepository.getLobbyByRuleId(ruleId)

            if (lobby != null) {
                if (lobby.userId == userId) {
                    return@run failure(MatchmakingError.SamePlayer)
                }
                val didLeave = it.lobbyRepository.leaveLobby(lobby.id, lobby.userId)
                if (!didLeave) {
                    return@run failure(MatchmakingError.LeaveLobbyFailed)
                }
                val playerBlack = if (Random.nextBoolean()) userId else lobby.userId
                val playerWhite = if (playerBlack == userId) lobby.userId else userId
                val gameId = it.gameRepository.createGame(ruleId, playerBlack, playerWhite)
                return@run success(Matchmaker(true, gameId))
            } else {
                return@run success(Matchmaker(false, it.lobbyRepository.createLobby(ruleId, userId)))
            }
        }
    }

    /**
     * Makes a move in the given game.
     * @param gameId id of the game
     * @param userId id of the user that wants to make the move
     * @param x x coordinate of the move
     * @param y y coordinate of the move
     * @return the result of the move
     * @see MakeMoveResult
     */
    fun makeMove(gameId: Int, userId: Int, x: Int, y: Int): MakeMoveResult {
        return transactionManager.run {
            val gameResult = getGameAndVerifyPlayer(gameId, userId, it)
            val game = when (gameResult) {
                is Failure -> return@run gameResult.value.resolveToMakeMoveResult()
                is Success -> gameResult.value
            }

            when (game) {
                is FinishedGame -> return@run failure(MakeMoveError.GameFinished)
                is OngoingGame -> {
                    if (x < 0 || x > game.rules.boardSize.maxIndex || y < 0 || y > game.rules.boardSize.maxIndex) {
                        return@run failure(MakeMoveError.ImpossiblePosition)
                    }
                    val currMove = Move(
                        position = Position(x, y),
                        cellColor = if (game.playerBlack == userId) {
                            CellColor.BLACK
                        } else {
                            CellColor.WHITE
                        }
                    )
                    val isValidMoveResult = game.rules.isValidMove(game.moveContainer, currMove, game.turn)
                    when (isValidMoveResult) {
                        is Failure -> return@run isValidMoveResult.value.resolveError()
                        is Success -> return@run resolveValidMove(game, currMove, it)
                    }
                }
            }
        }
    }

    /**
     * Resolves a valid move.
     * @param game the game
     * @param move the move to be made
     * @param tr the transaction context
     * @return the result of the move
     * @see MakeMoveResult
     */
    private fun resolveValidMove(game: OngoingGame, move: Move, tr: Transaction): MakeMoveResult {
        if (game.rules.isWinningMove(game.moveContainer, move)) {
            if (!tr.gameRepository.addToMoveArray(game.id, move.position.toIndex(game.rules.boardSize.maxIndex))) {
                return failure(MakeMoveError.MakeMoveFailed)
            }

            tr.gameRepository.setGameState(game.id, GameState.FINISHED)
            tr.gameRepository.setGameOutcome(game.id, move.cellColor.toGameOutcome())

            val winnerId = if (move.cellColor == CellColor.BLACK) game.playerBlack else game.playerWhite
            val loserId = if (move.cellColor == CellColor.BLACK) game.playerWhite else game.playerBlack

            updatePlayerStats(winnerId, loserId, game.rules.ruleId, tr, RankingUserData.WIN)
        } else {
            if (!(tr.gameRepository.addToMoveArray(game.id, move.position.toIndex(game.rules.boardSize.maxIndex)))) {
                return failure(MakeMoveError.MakeMoveFailed)
            }
            val moveContainer = game.moveContainer.addMove(move) ?: return failure(MakeMoveError.MakeMoveFailed)
            if (moveContainer.isFull()) {
                tr.gameRepository.setGameState(game.id, GameState.FINISHED)
                tr.gameRepository.setGameOutcome(game.id, GameOutcome.DRAW)

                updatePlayerStats(game.playerBlack, game.playerWhite, game.rules.ruleId, tr, RankingUserData.DRAW)
            }
        }
        val newGame = tr.gameRepository.getGameById(game.id)
            ?: return failure(MakeMoveError.GameNotFound) // should never return because the game exists
        return success(newGame)
    }

    /**
     * Updates the statistics of two players based on the game outcome.
     * @param player1Id The ID of the first player.
     * @param player2Id The ID of the second player.
     * @param ruleId The rules set being followed in the game.
     * @param tr The transaction context.
     * @param player1Score The score/result of the first player after the game.
     */
    private fun updatePlayerStats(
        player1Id: Int,
        player2Id: Int,
        ruleId: Int,
        tr: Transaction,
        player1Score: Double
    ) {
        val player1 = tr.usersRepository.getUserById(player1Id) ?: return // should never return because the user exists
        val player2 = tr.usersRepository.getUserById(player2Id) ?: return // should never return because the user exists
        val player1Stats = tr.usersRepository.getUserRanking(player1Id, ruleId)
            ?: RankingUserData(player1Id, player1.username, ruleId)
        val player2Stats = tr.usersRepository.getUserRanking(player2Id, ruleId)
            ?: RankingUserData(player2Id, player2.username, ruleId)

        val newPlayer1Elo = updateElo(player1Stats.elo.toDouble(), player2Stats.elo.toDouble(), player1Score)
        val newPlayer2Elo = updateElo(player2Stats.elo.toDouble(), player1Stats.elo.toDouble(), 1 - player1Score)

        tr.usersRepository.setUserRanking(
            userId = player1Id,
            rankingUserData = player1Stats.copy(gamesPlayed = player1Stats.gamesPlayed + 1, elo = newPlayer1Elo.toInt())
        )
        tr.usersRepository.setUserRanking(
            userId = player2Id,
            rankingUserData = player2Stats.copy(gamesPlayed = player2Stats.gamesPlayed + 1, elo = newPlayer2Elo.toInt())
        )
    }

    /**
     * Gets the available rules.
     * This function doesn't use limit and offset because there are only a limited number of rules that can be delivered at once.
     * @return a list of the available rules
     */
    fun getAvailableRules(): List<Rules> =
        transactionManager.run {
            it.gameRepository.getAllRules()
        }

    /**
     * Gets the id of the player that has the turn in a given game.
     * @param gameId id of the game
     * @param userId id of the user that wants to know the current turn
     * @return the id of the player that has the turn, or null if the game doesn't exist
     */
    fun getCurrentTurnPlayerId(gameId: Int, userId: Int): CurrentTurnPlayerResult {
        return transactionManager.run {
            val players =
                it.gameRepository.getGamePlayers(gameId) ?: return@run failure(CurrentTurnPlayerError.GameNotFound)
            val currentColor =
                it.gameRepository.getTurn(gameId) ?: return@run failure(CurrentTurnPlayerError.GameAlreadyFinished)
            if (players.first != userId && players.second != userId) {
                return@run failure(CurrentTurnPlayerError.PlayerNotInGame)
            }
            when (currentColor) {
                CellColor.BLACK -> success(CurrentTurnPlayerOutput(players.first))
                CellColor.WHITE -> success(CurrentTurnPlayerOutput(players.second))
            }
        }
    }

    /**
     * Gets the finished games of a user.
     * @param offset the offset for the game list
     * @param limit the limit for the game list
     * @param userId the id of the user to get the games from
     * @return the list of [FinishedGame]
     */
    fun getUserFinishedGames(
        offset: Int? = null,
        limit: Int? = null,
        userId: Int
    ): GetUserFinishedGamesResult =
        transactionManager.run {
            val finishedGames = it.gameRepository.getUserFinishedGames(
                offset ?: DEFAULT_OFFSET,
                limit ?: DEFAULT_LIMIT,
                userId
            )
            val totalCount = it.gameRepository.getUserFinishedGamesCount(userId)
            Pair(finishedGames, totalCount)
        }

    /**
     * Gets the details of a game.
     * @param gameId The id of the game.
     * @param userId The id of the user.
     * @return The details of the game, or an error.
     */
    fun getGame(gameId: Int, userId: Int): GetGameResult =
        transactionManager.run {
            getGameAndVerifyPlayer(gameId, userId, it)
        }

    /**
     * Forfeits a game.
     * @param gameId The id of the game.
     * @param userId The id of the user.
     * @return The details of the game, or an error.
     */
    fun forfeitGame(gameId: Int, userId: Int): ForfeitGameResult =
        transactionManager.run { tr ->
            val gameResult = getGameAndVerifyPlayer(gameId, userId, tr)
            val game = when (gameResult) {
                is Failure -> return@run gameResult.value.resolveToForfeitResult()
                is Success -> gameResult.value
            }
            if (game !is OngoingGame) {
                return@run failure(ForfeitGameError.GameAlreadyFinished)
            }
            val winnerId = if (game.playerBlack == userId) game.playerWhite else game.playerBlack
            val loserId = if (game.playerBlack == userId) game.playerBlack else game.playerWhite
            tr.gameRepository.setGameState(game.id, GameState.FINISHED)
            val gameOutcome = if (game.playerBlack == userId) GameOutcome.WHITE_WON else GameOutcome.BLACK_WON
            tr.gameRepository.setGameOutcome(game.id, gameOutcome)
            updatePlayerStats(winnerId, loserId, game.rules.ruleId, tr, RankingUserData.WIN)
            val newGame = tr.gameRepository.getGameById(game.id)
                ?: return@run failure(ForfeitGameError.GameNotFound) // should never return because the game exists
            return@run success(newGame as FinishedGame)
        }

    /**
     * Gets the details of a game and verifies that the user is in the game.
     * @param gameId The id of the game.
     * @param userId The id of the user.
     * @param tr The transaction context.
     * @return The details of the game, or an error.
     */
    private fun getGameAndVerifyPlayer(gameId: Int, userId: Int, tr: Transaction): GetGameResult {
        if (!tr.usersRepository.isUserStoredById(userId)) {
            return failure(GetGameError.PlayerNotFound)
        }
        val gamePlayers = tr.gameRepository.getGamePlayers(gameId)
            ?: return failure(GetGameError.GameNotFound)

        if (gamePlayers.first != userId && gamePlayers.second != userId) {
            return failure(GetGameError.PlayerNotInGame)
        }
        return success(tr.gameRepository.getGameById(gameId)!!)
    }

    /**
     * Resolves a [GetGameError] (getGameAndVerifyPlayer function error) into a [MakeMoveError] (GameService error).
     */
    private fun GetGameError.resolveToMakeMoveResult() =
        when (this) {
            is GetGameError.PlayerNotInGame -> failure(MakeMoveError.PlayerNotInGame)
            is GetGameError.PlayerNotFound -> failure(MakeMoveError.PlayerNotFound)
            is GetGameError.GameNotFound -> failure(MakeMoveError.GameNotFound)
        }

    /**
     * Resolves a [GetGameError] (getGameAndVerifyPlayer function error) into a [ForfeitGameError] (GameService error).
     */
    private fun GetGameError.resolveToForfeitResult() =
        when (this) {
            is GetGameError.PlayerNotInGame -> failure(ForfeitGameError.PlayerNotInGame)
            is GetGameError.PlayerNotFound -> failure(ForfeitGameError.PlayerNotFound)
            is GetGameError.GameNotFound -> failure(ForfeitGameError.GameNotFound)
        }

    /**
     * Resolves a [MoveError] (isValidMove function error) into a [MakeMoveError] (GameService error).
     */
    private fun MoveError.resolveError() =
        when (this) {
            is MoveError.InvalidPosition -> failure(MakeMoveError.ImpossiblePosition)
            is MoveError.AlreadyOccupied -> failure(MakeMoveError.AlreadyOccupied)
            is MoveError.InvalidTurn -> failure(MakeMoveError.InvalidTurn)
        }

    /**
     * Converts a [Position] to an index.
     * @param size The size of the board.
     * @return The index of the position.
     */
    private fun Position.toIndex(size: Int): Int {
        return this.y * (size + 1) + this.x
    }

    companion object {
        private const val DEFAULT_OFFSET = 0
        private const val DEFAULT_LIMIT = 10
    }
}
