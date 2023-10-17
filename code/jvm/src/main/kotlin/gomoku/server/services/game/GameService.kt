package gomoku.server.services.game

import gomoku.server.domain.game.Matchmaker
import gomoku.server.domain.game.match.Color
import gomoku.server.domain.game.match.FinishedMatch
import gomoku.server.domain.game.match.MatchOutcome
import gomoku.server.domain.game.match.MatchState
import gomoku.server.domain.game.match.Move
import gomoku.server.domain.game.match.OngoingMatch
import gomoku.server.domain.game.match.Position
import gomoku.server.domain.game.match.toMatchOutcome
import gomoku.server.domain.game.rules.MoveError
import gomoku.server.domain.game.rules.Rules
import gomoku.server.domain.user.UserRuleStats
import gomoku.server.domain.user.updateElo
import gomoku.server.repository.Transaction
import gomoku.server.repository.TransactionManager
import gomoku.server.services.errors.game.MakeMoveError
import gomoku.server.services.errors.game.MatchNotFoundError
import gomoku.server.services.errors.game.MatchmakingError
import gomoku.utils.Either
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
                    failure(MatchmakingError.SamePlayer)
                }
                it.lobbyRepository.leaveLobby(lobby.userId)
                val playerBlack = if (Random.nextBoolean()) userId else lobby.userId
                val playerWhite = if (playerBlack == userId) lobby.userId else userId
                val matchId = it.matchRepository.createMatch(ruleId, playerBlack, playerWhite)
                success(Matchmaker(true, matchId))
            } else {
                success(Matchmaker(false, it.lobbyRepository.createLobby(ruleId, userId)))
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
                        is Either.Left -> return@run isValidMoveResult.value.resolveError()
                        is Either.Right -> return@run resolveValidMove(match, currMove, it)
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
            if (!tr.matchRepository.addToMoveArray(match.matchId, move.position.value)) {
                return failure(MakeMoveError.MakeMoveFailed)
            }
            tr.matchRepository.setMatchState(match.matchId, MatchState.FINISHED)
            tr.matchRepository.setMatchOutcome(match.matchId, move.color.toMatchOutcome())

            val winnerId = if (move.color == Color.BLACK) match.playerBlack else match.playerWhite
            val loserId = if (move.color == Color.BLACK) match.playerWhite else match.playerBlack

            updatePlayerStats(winnerId, loserId, match.rules.ruleId, tr, UserRuleStats.WIN)
        } else {
            if (!tr.matchRepository.addToMoveArray(match.matchId, move.position.value)) {
                return failure(MakeMoveError.MakeMoveFailed)
            }
            if (match.moveContainer.isFull()) {
                tr.matchRepository.setMatchState(match.matchId, MatchState.FINISHED)
                tr.matchRepository.setMatchOutcome(match.matchId, MatchOutcome.DRAW)

                updatePlayerStats(match.playerBlack, match.playerWhite, match.rules.ruleId, tr, UserRuleStats.DRAW)
            }
        }
        val newGame = tr.matchRepository.getMatchById(match.matchId)
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
        val statsPlayer1 = tr.usersRepository.getUserRanking(player1Id, ruleId) ?: UserRuleStats(ruleId)
        val statsPlayer2 = tr.usersRepository.getUserRanking(player2Id, ruleId) ?: UserRuleStats(ruleId)

        val newPlayer1Elo = updateElo(statsPlayer1.elo.toDouble(), statsPlayer2.elo.toDouble(), player1Score)
        val newPlayer2Elo = updateElo(statsPlayer2.elo.toDouble(), statsPlayer1.elo.toDouble(), 1 - player1Score)

        tr.usersRepository.setUserRuleStats(
            userId = player1Id,
            userStatsData = statsPlayer1.copy(gamesPlayed = statsPlayer1.gamesPlayed + 1, elo = newPlayer1Elo.toInt())
        )
        tr.usersRepository.setUserRuleStats(
            userId = player2Id,
            userStatsData = statsPlayer2.copy(gamesPlayed = statsPlayer2.gamesPlayed + 1, elo = newPlayer2Elo.toInt())
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
        }

    /**
     * Leaves the matchmaking process.
     * @param userId id of the user
     * @return true if the user was removed from
     * the matchmaking process, false otherwise
     */
    fun leaveLobby(userId: Int): Boolean {
        return transactionManager.run {
            it.lobbyRepository.leaveLobby(userId)
        }
    }

    /**
     * Gets the available rules.
     * This function doesn't use limit and offset because there are only a limited number of rules that can be delivered at once.
     * @return a list of the available rules
     */
    fun getAvailableRules(): List<Rules> {
        return transactionManager.run {
            it.matchRepository.getAllRules()
        }
    }

    /**
     * Gets the id of the player that has the turn in a given match.
     * @param matchId id of the match
     * @return the id of the player that has the turn, or null if the match doesn't exist
     */
    fun getCurrentTurnPlayerId(matchId: Int): Int? {
        return transactionManager.run {
            val currentColor = it.matchRepository.getTurn(matchId)
            val players = it.matchRepository.getMatchPlayers(matchId)
            if (players != null) {
                if (currentColor == Color.BLACK) {
                    players.first
                } else {
                    players.second
                }
            } else {
                null
            }
        }
    }

    /**
     * TODO should this be a part of get match details?
     */
    fun getMatchState(matchId: Int, userId: Int): MatchState? {
        return transactionManager.run {
            TODO()
        }
    }

    // TODO is state needed if so in what context
    /**
     *
     */
    fun getUserFinishedMatches(offset: Int, limit: Int, userId: Int): List<FinishedMatch> {
        return transactionManager.run {
            TODO()
        }
    }

    /**
     * Gets the details of a game.
     * @param gameId The id of the game.
     * @return The details of the game.
     */
    fun getGame(gameId: Int): GetMatchResult =
        transactionManager.run {
            val match = it.matchRepository.getMatchById(gameId)
            when (match) {
                null -> return@run failure(MatchNotFoundError.GameMatchNotFound)
                is FinishedMatch -> return@run success(match)
                is OngoingMatch -> return@run success(match)
            }
        }
}
