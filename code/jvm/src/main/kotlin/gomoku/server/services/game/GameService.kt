package gomoku.server.services.game

import gomoku.server.domain.game.Matchmaker
import gomoku.server.domain.user.User
import gomoku.server.repository.TransactionManager
import gomoku.server.services.errors.game.MatchmakingError
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

//    //TODO("recieve gameId and then we get game")
//    fun makeMove(game: Match, move: Move): MakeMoveResult {
//            return transactionManager.run {
//                //val game = it.matchRepository.getMatchById(gameId)
//                if (game.rules.isValidMove(game.moveContainer.getMoves()/*game.moves*/, move)) {
//
//                    if (game.rules.isWinningMove(game.moveContainer.getMoves(), move)) {
//                        it.matchRepository.makeMove(game.matchId, move)
//                        it.matchRepository.setMatchState(game.matchId, MatchState.FINISHED)
//                        val newGame = it.matchRepository.getMatchById(game.matchId)
//                        return@run success(newGame)
//                    } else {
//                        it.matchRepository.makeMove(game.matchId, move)
//                    }
//                    val newGame = it.matchRepository.getMatchById(game.matchId)
//
//                    val newGame = game.moveContainer.addMove(move)
//                    success(newGame)
//                } else {
//                    failure(MakeMoveError.InvalidMove)
//                }
//            }
//    }
//
//    fun getLobbyInfoByUser(user: User): Lobby? {
//        return transactionManager.run {
//            it.lobbyRepository.getLobbyByUser(user)
//        }
//    }
//
//    fun createOrJoinMatch(rule: Rules, userId: Int): Int {
//        val ruleId = matchRepository.getRuleId(rule)
//        // Let's first try to find an existing match with the WAITING_PLAYER state.
//        val matches = matchRepository.getAllLobbies() // This can be optimized with a suitable repository function
//        val waitingMatch = matches.find { it.state == MatchState.WAITING_PLAYER }
//        if (waitingMatch != null) {
//            return matchRepository.joinUserToMatch(waitingMatch.id, userId)
//        } else {
//            return matchRepository.createMatch(ruleId, userId)
//        }
//    }
//
//    fun initiateMatchWithPlayers(playerA: Player, playerB: Player): Pair<Player, Player> {
//        return matchRepository.initiateMatch(playerA, playerB)
//    }
//
//    fun makeMoveAndGetUpdatedMoves(matchId: Int, move: Move): List<Move> {
//        matchRepository.makeMove(matchId, move)
//        return matchRepository.getAllMoves(matchId)
//    }
//
//    fun getCurrentTurn(matchId: Int): Player? {
//        val currentColor = matchRepository.getTurn(matchId)
//        val players = matchRepository.getMatchPlayers(matchId)
//        return when (currentColor) {
//            Color.BLACK -> players?.first
//            Color.WHITE -> players?.second
//        }
//    }
//
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
