package gomoku.server.services.game

import gomoku.server.domain.game.Matchmaker
import gomoku.server.domain.game.match.FinishedMatch
import gomoku.server.domain.game.match.MatchOutcome
import gomoku.server.domain.game.match.MatchState
import gomoku.server.domain.game.match.Move
import gomoku.server.domain.game.match.OngoingMatch
import gomoku.server.domain.game.match.toMatchOutcome
import gomoku.server.domain.game.player.Color
import gomoku.server.domain.game.player.UserRuleStats
import gomoku.server.domain.game.player.updateElo
import gomoku.server.domain.game.rules.MoveError
import gomoku.server.domain.game.rules.Rules
import gomoku.server.domain.user.User
import gomoku.server.repository.TransactionManager
import gomoku.server.services.errors.game.MakeMoveError
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
     * @param user user that wants to play
     * @return the result of the matchmaking process
     * @see MatchmakingResult
     */
    fun startMatchmakingProcess(ruleId: Int, user: User): MatchmakingResult {
        return transactionManager.run {
            val lobby = it.lobbyRepository.getLobbyByRuleId(ruleId)

            if (lobby != null) {
                if (lobby.user.uuid == user.uuid) {
                    failure(MatchmakingError.SamePlayer)
                }
                it.lobbyRepository.leaveLobby(lobby.user.uuid)
                val playerBlack = if (Random.nextBoolean()) user.uuid else lobby.user.uuid
                val playerWhite = if (playerBlack == user.uuid) lobby.user.uuid else user.uuid
                val matchId = it.matchRepository.createMatch(ruleId, playerBlack, playerWhite)
                success(Matchmaker(true, matchId))
            } else {
                success(Matchmaker(false, it.lobbyRepository.createLobby(ruleId, user.uuid)))
            }
        }
    }

    /**
     * Makes a move in the given match.
     * @param gameId id of the match
     * @param move move to be made
     * @return the result of the move
     * @see MakeMoveResult
     */
    fun makeMove(gameId: Int, move: Move): MakeMoveResult {
        return transactionManager.run {
            val match = it.matchRepository.getMatchById(gameId) ?: return@run failure(MakeMoveError.GameNotFound)
            when (match) {
                is FinishedMatch -> return@run failure(MakeMoveError.GameFinished)
                is OngoingMatch -> {
                    val isValidMoveResult = match.rules.isValidMove(match.moveContainer, move, match.turn)
                    when(isValidMoveResult) {
                        is Either.Left -> {
                            when(isValidMoveResult.value) {
                                is MoveError.ImpossiblePosition -> return@run failure(MakeMoveError.ImpossiblePosition)
                                is MoveError.AlreadyOccupied -> return@run failure(MakeMoveError.AlreadyOccupied)
                                is MoveError.InvalidTurn -> return@run failure(MakeMoveError.InvalidTurn)
                            }
                        }
                        is Either.Right -> {
                            if (match.rules.isWinningMove(match.moveContainer, move)) {
                                if (!it.matchRepository.makeMove(match.matchId, move)) return@run failure(MakeMoveError.MakeMoveFailed)
                                it.matchRepository.setMatchState(match.matchId, MatchState.FINISHED)
                                it.matchRepository.setMatchOutcome(match.matchId, move.color.toMatchOutcome())

                                val winnerId = if (move.color == Color.BLACK) match.playerBlack else match.playerWhite
                                val loserId = if (move.color == Color.BLACK) match.playerWhite else match.playerBlack

                                val statsWinner = it.usersRepository.getUserStatsByRule(winnerId , match.rules.ruleId) ?:
                                    UserRuleStats(match.rules)
                                val statsLoser = it.usersRepository.getUserStatsByRule(loserId, match.rules.ruleId) ?:
                                    UserRuleStats(match.rules)

                                val newWinnerElo = updateElo(statsWinner.elo.toDouble(), statsLoser.elo.toDouble(), 1.0)
                                val newLoserElo = updateElo(statsLoser.elo.toDouble(), statsWinner.elo.toDouble(), 0.0)

                                it.usersRepository.setUserRuleStats(winnerId, statsWinner.copy(gamesPlayed = statsWinner.gamesPlayed + 1, elo = newWinnerElo.toInt()))
                                it.usersRepository.setUserRuleStats(loserId, statsLoser.copy(gamesPlayed = statsLoser.gamesPlayed + 1, elo = newLoserElo.toInt()))
                            } else {
                                if (!it.matchRepository.makeMove(match.matchId, move)) return@run failure(MakeMoveError.MakeMoveFailed)
                                if (match.moveContainer.isFull()) {
                                    it.matchRepository.setMatchState(match.matchId, MatchState.FINISHED)
                                    it.matchRepository.setMatchOutcome(match.matchId, MatchOutcome.DRAW)

                                    val statsBlack = it.usersRepository.getUserStatsByRule(match.playerBlack , match.rules.ruleId) ?:
                                        UserRuleStats(match.rules, 1, 0)
                                    val statsWhite = it.usersRepository.getUserStatsByRule(match.playerWhite, match.rules.ruleId) ?:
                                        UserRuleStats(match.rules, 0, 1)

                                    val newBlackElo = updateElo(statsBlack.elo.toDouble(), statsWhite.elo.toDouble(), 0.5)
                                    val newWhiteElo = updateElo(statsWhite.elo.toDouble(), statsBlack.elo.toDouble(), 0.5)

                                    it.usersRepository.setUserRuleStats(match.playerBlack, statsBlack.copy(gamesPlayed = statsBlack.gamesPlayed + 1, elo = newBlackElo.toInt()))
                                    it.usersRepository.setUserRuleStats(match.playerWhite, statsWhite.copy(gamesPlayed = statsWhite.gamesPlayed + 1,elo = newWhiteElo.toInt()))
                                }
                            }
                            val newGame = it.matchRepository.getMatchById(match.matchId) ?: return@run failure(MakeMoveError.GameNotFound)
                            return@run success(newGame)
                        }
                    }
                }
            }
        }
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

    fun getAvailableRules(offset: Int, limit: Int): List<Rules> {
        TODO()
    }

//    fun getCurrentTurnPlayerId(matchId: Int): Int? {
//        return transactionManager.run {
//            val currentColor = it.matchRepository.getTurn(matchId)
//            val players = it.matchRepository.getMatchPlayers(matchId)
//            return when (currentColor) {
//                Color.BLACK -> players?.first
//                Color.WHITE -> players?.second
//            }
//        }
//
//    }

//    fun getMatchInfo(matchId: Int): MatchInfo? {
//        val match = matchRepository.getMatchById(matchId)
//        val state = matchRepository.getMatchState(matchId)
//        val rule = matchRepository.getMatchRule(matchId)
//        val players = matchRepository.getMatchPlayers(matchId)
//        return if (match != null && players != null) {
//            MatchInfo(match, state, rule, players)
//        } else {
//            null
//        }
//    }
//
//    data class MatchInfo(
//        val match: Match,
//        val state: MatchState,
//        val rule: Rules,
//        val players: Pair<Player, Player>
//    )
}
