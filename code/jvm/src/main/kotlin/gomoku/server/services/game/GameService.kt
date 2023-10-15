package gomoku.server.services.game

import gomoku.server.domain.game.Lobby
import gomoku.server.domain.game.Matchmaker
import gomoku.server.domain.game.match.Match
import gomoku.server.domain.game.match.MatchOutcome
import gomoku.server.domain.game.match.MatchState
import gomoku.server.domain.game.player.Move
import gomoku.server.domain.game.player.Player
import gomoku.server.domain.game.rules.Rules
import gomoku.server.domain.user.User
import gomoku.server.services.errors.game.MatchmakingError
import gomoku.server.repository.TransactionManager
import gomoku.server.services.errors.game.MakeMoveError
import gomoku.utils.failure
import gomoku.utils.success
import org.springframework.stereotype.Service

/**
 * Service for game-related operations
 * @param transactionManager The transaction manager
 */
@Service
class GameService(private val transactionManager: TransactionManager) {

    fun startMatchmakingProcess(rule: Rules, user: User): MatchmakingResult {
        return transactionManager.run {
            val lobby = it.lobbyRepository.getLobbyByRule(rule)

            if (lobby != null) {
                if (lobby.user.uuid == user.uuid) {
                    failure(MatchmakingError.SamePlayer)
                }
                it.lobbyRepository.leaveLobby(lobby.user.uuid)
                val ruleId = it.matchRepository.getRuleId(rule)
                val matchId = it.matchRepository.createMatch(ruleId, user.uuid)
                success(Matchmaker(true, matchId))
            } else {
                success(Matchmaker(false ,it.lobbyRepository.joinLobby(rule, user.uuid)))
            }
        }
    }

    fun makeMove(game: Match, move: Move): MakeMoveResult {
        return if (game.rules.isValidMove(game.board.getMoves(), move)) {
            transactionManager.run {
                if (game.rules.isWinningMove(game.board.getMoves(), move)) {
                    it.matchRepository.makeMove(game.matchId, move)
                    it.matchRepository.setMatchState(game.matchId, MatchState.FINISHED)
                    val newGame = it.matchRepository.getMatchById(game.matchId)
                    return@run success(newGame)
                } else {
                    it.matchRepository.makeMove(game.matchId, move)
                }
                val newGame = it.matchRepository.getMatchById(game.matchId)
            }
            val newGame = game.board.addMove(move)
            success(newGame)
        } else {
            failure(MakeMoveError.InvalidMove)
        }
    }

    fun getLobbyInfoByUser(user: User): Lobby? {
        return transactionManager.run {
            it.lobbyRepository.getLobbyByUser(user)
        }
    }

    fun createOrJoinMatch(rule: Rules, userId: Int): Int {
        val ruleId = matchRepository.getRuleId(rule)
        // Let's first try to find an existing match with the WAITING_PLAYER state.
        val matches = matchRepository.getAllLobbies() // This can be optimized with a suitable repository function
        val waitingMatch = matches.find { it.state == MatchState.WAITING_PLAYER }
        if (waitingMatch != null) {
            return matchRepository.joinUserToMatch(waitingMatch.id, userId)
        } else {
            return matchRepository.createMatch(ruleId, userId)
        }
    }

    fun initiateMatchWithPlayers(playerA: Player, playerB: Player): Pair<Player, Player> {
        return matchRepository.initiateMatch(playerA, playerB)
    }

    fun makeMoveAndGetUpdatedMoves(matchId: Int, move: Move): List<Move> {
        matchRepository.makeMove(matchId, move)
        return matchRepository.getAllMoves(matchId)
    }

    fun getCurrentTurn(matchId: Int): Player? {
        val currentColor = matchRepository.getTurn(matchId)
        val players = matchRepository.getMatchPlayers(matchId)
        return when (currentColor) {
            Color.BLACK -> players?.first
            Color.WHITE -> players?.second
        }
    }

    fun getMatchInfo(matchId: Int): MatchInfo? {
        val match = matchRepository.getMatchById(matchId)
        val state = matchRepository.getMatchState(matchId)
        val rule = matchRepository.getMatchRule(matchId)
        val players = matchRepository.getMatchPlayers(matchId)
        return if (match != null && players != null) {
            MatchInfo(match, state, rule, players)
        } else {
            null
        }
    }

    data class MatchInfo(
        val match: Match,
        val state: MatchState,
        val rule: Rules,
        val players: Pair<Player, Player>
    )
}