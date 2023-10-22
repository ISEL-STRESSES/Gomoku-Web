package gomoku.server.services.game

import gomoku.server.domain.game.Matchmaker
import gomoku.server.domain.game.errors.MoveError
import gomoku.server.domain.game.match.Color
import gomoku.server.domain.game.match.FinishedMatch
import gomoku.server.domain.game.match.MatchOutcome
import gomoku.server.domain.game.match.MatchState
import gomoku.server.domain.game.match.Move
import gomoku.server.domain.game.match.OngoingMatch
import gomoku.server.domain.game.match.Position
import gomoku.server.domain.game.match.toMatchOutcome
import gomoku.server.domain.game.rules.Rules
import gomoku.server.domain.user.RankingUserData
import gomoku.server.domain.user.updateElo
import gomoku.server.repository.Transaction
import gomoku.server.repository.TransactionManager
import gomoku.server.services.errors.game.CurrentTurnPlayerError
import gomoku.server.services.errors.game.GetMatchError
import gomoku.server.services.errors.game.LeaveLobbyError
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
                val didLeave = it.lobbyRepository.leaveLobby(lobby.userId)
                if (!didLeave) {
                    return@run failure(MatchmakingError.LeaveLobbyFailed)
                }
                val playerBlack = if (Random.nextBoolean()) userId else lobby.userId
                val playerWhite = if (playerBlack == userId) lobby.userId else userId
                val matchId = it.matchRepository.createMatch(ruleId, playerBlack, playerWhite)
                return@run success(Matchmaker(true, matchId))
            } else {
                return@run success(Matchmaker(false, it.lobbyRepository.createLobby(ruleId, userId)))
            }
        }
    }

    /**
     * Makes a move in the given match.
     * @param gameId id of the match
     * @param userId id of the user that wants to make the move
     * @param pos position of the move
     * @return the result of the move
     * @see MakeMoveResult
     */
    fun makeMove(gameId: Int, userId: Int, pos: Int): MakeMoveResult {
        return transactionManager.run {
            val match = it.matchRepository.getMatchById(gameId)
                ?: return@run failure(MakeMoveError.GameNotFound)
            when (match) {
                is FinishedMatch -> return@run failure(MakeMoveError.GameFinished)
                is OngoingMatch -> {
                    val currMove = Move(
                        position = Position(pos),
                        color = if (match.playerBlack == userId) {
                            Color.BLACK
                        } else {
                            Color.WHITE
                        }
                    )
                    val isValidMoveResult = match.rules.isValidMove(match.moveContainer, currMove, match.turn)
                    when (isValidMoveResult) {
                        is Failure -> return@run isValidMoveResult.value.resolveError()
                        is Success -> return@run resolveValidMove(match, currMove, it)
                    }
                }
            }
        }
    }

    /**
     * Resolves a valid move.
     * @param match the match
     * @param move the move to be made
     * @param tr the transaction context
     * @return the result of the move
     * @see MakeMoveResult
     */
    private fun resolveValidMove(match: OngoingMatch, move: Move, tr: Transaction): MakeMoveResult {
        if (match.rules.isWinningMove(match.moveContainer, move)) {
            if (!tr.matchRepository.addToMoveArray(match.id, move.position.value)) {
                return failure(MakeMoveError.MakeMoveFailed)
            }
            tr.matchRepository.setMatchState(match.id, MatchState.FINISHED)
            tr.matchRepository.setMatchOutcome(match.id, move.color.toMatchOutcome())

            val winnerId = if (move.color == Color.BLACK) match.playerBlack else match.playerWhite
            val loserId = if (move.color == Color.BLACK) match.playerWhite else match.playerBlack

            updatePlayerStats(winnerId, loserId, match.rules.ruleId, tr, RankingUserData.WIN)
        } else {
            if (!tr.matchRepository.addToMoveArray(match.id, move.position.value)) {
                return failure(MakeMoveError.MakeMoveFailed)
            }
            if (match.moveContainer.isFull()) {
                tr.matchRepository.setMatchState(match.id, MatchState.FINISHED)
                tr.matchRepository.setMatchOutcome(match.id, MatchOutcome.DRAW)

                updatePlayerStats(match.playerBlack, match.playerWhite, match.rules.ruleId, tr, RankingUserData.DRAW)
            }
        }
        val newGame = tr.matchRepository.getMatchById(match.id)
            ?: return failure(MakeMoveError.GameNotFound)
        return success(newGame)
    }

    /**
     * Updates the statistics of two players based on the game outcome.
     * @param player1Id The ID of the first player.
     * @param player2Id The ID of the second player.
     * @param ruleId The rules set being followed in the match.
     * @param tr The transaction context.
     * @param player1Score The score/result of the first player after the match.
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
        val statsPlayer1 = tr.usersRepository.getUserRanking(player1Id, ruleId)
            ?: RankingUserData(player1Id, player1.username, ruleId)
        val statsPlayer2 = tr.usersRepository.getUserRanking(player2Id, ruleId)
            ?: RankingUserData(player2Id, player2.username, ruleId)

        val newPlayer1Elo = updateElo(statsPlayer1.elo.toDouble(), statsPlayer2.elo.toDouble(), player1Score)
        val newPlayer2Elo = updateElo(statsPlayer2.elo.toDouble(), statsPlayer1.elo.toDouble(), 1 - player1Score)

        tr.usersRepository.setUserRanking(
            userId = player1Id,
            rankingUserData = statsPlayer1.copy(gamesPlayed = statsPlayer1.gamesPlayed + 1, elo = newPlayer1Elo.toInt())
        )
        tr.usersRepository.setUserRanking(
            userId = player2Id,
            rankingUserData = statsPlayer2.copy(gamesPlayed = statsPlayer2.gamesPlayed + 1, elo = newPlayer2Elo.toInt())
        )
    }

    /**
     * Resolves a [MoveError] (isValidMove function error) into a [MakeMoveError] (GameService error).
     */
    private fun MoveError.resolveError() =
        when (this) {
            is MoveError.ImpossiblePosition -> failure(MakeMoveError.ImpossiblePosition)
            is MoveError.AlreadyOccupied -> failure(MakeMoveError.AlreadyOccupied)
            is MoveError.InvalidTurn -> failure(MakeMoveError.InvalidTurn)
            is MoveError.InvalidMove -> failure(MakeMoveError.InvalidMove)
        }

    /**
     * Leaves the matchmaking process.
     * @param userId id of the user
     * @return an empty [Success] if the user left the matchmaking process, a [Failure] otherwise
     */
    fun leaveLobby(lobbyId: Int, userId: Int): LeaveLobbyResult =
        transactionManager.run {
            val lobby = it.lobbyRepository.getLobbyById(lobbyId) ?: return@run failure(LeaveLobbyError.LobbyNotFound) //TODO: Create tests for this function

            if(lobby.userId != userId) return@run failure(LeaveLobbyError.UserNotInLobby)

            if(it.lobbyRepository.leaveLobby(userId))
                return@run success(Unit)
            else
                return@run failure(LeaveLobbyError.LeaveLobbyFailed)
        }

    /**
     * Gets the available rules.
     * This function doesn't use limit and offset because there are only a limited number of rules that can be delivered at once.
     * @return a list of the available rules
     */
    fun getAvailableRules(): List<Rules> =
        transactionManager.run {
            it.matchRepository.getAllRules()
        }

    /**
     * Gets the id of the player that has the turn in a given match.
     * @param matchId id of the match
     * @return the id of the player that has the turn, or null if the match doesn't exist
     */
    fun getCurrentTurnPlayerId(matchId: Int): CurrentTurnPlayerResult {
        return transactionManager.run {
            val currentColor = it.matchRepository.getTurn(matchId) ?: return@run failure(CurrentTurnPlayerError.NoTurn)
            val players = it.matchRepository.getMatchPlayers(matchId) ?: return@run failure(CurrentTurnPlayerError.MatchNotFound)
            when (currentColor) {
                Color.BLACK -> success(players.first)
                Color.WHITE -> success(players.second)
            }
        }
    }

    /**
     * Gets the finished matches of a user.
     * @param offset the offset for the matches list
     * @param limit the limit for the matches list
     * @param userId the id of the user to get the matches from
     * @return the list of [FinishedMatch]
     */
    fun getUserFinishedMatches(offset: Int = DEFAULT_OFFSET, limit: Int = DEFAULT_LIMIT, userId: Int): List<FinishedMatch> =
        transactionManager.run {
            it.matchRepository.getUserFinishedMatches(offset, limit, userId)
        }

    /**
     * Gets the details of a game.
     * @param gameId The id of the game.
     * @return The details of the game, or null if the game doesn't exist.
     */
    fun getGame(gameId: Int): Match? =
        transactionManager.run {
            it.matchRepository.getMatchById(gameId)
        }

    companion object {
        private const val DEFAULT_OFFSET = 0
        private const val DEFAULT_LIMIT = 10
    }
}
