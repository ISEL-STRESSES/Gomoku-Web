package gomoku.server.services.game

import gomoku.server.domain.game.Lobby
import gomoku.server.domain.game.Matchmaker
import gomoku.server.domain.game.match.Match
import gomoku.server.domain.game.match.MatchState
import gomoku.server.domain.game.player.Move
import gomoku.server.domain.game.player.Player
import gomoku.server.domain.game.rules.Rules
import gomoku.server.domain.user.User
import gomoku.server.services.errors.MatchmakingError
import gomoku.server.repository.TransactionManager
import gomoku.utils.failure
import org.springframework.stereotype.Service

@Service
class GameService(private val transactionManager: TransactionManager) {

    fun startMatchmakingProcess(rulesId: Int, user: User): MatchMakingResult {
        return transactionManager.run {
            val lobby = it.lobbyRepository.getLobbyByRuleId(rulesId)

            if (lobby != null) {

                if (lobby.user.uuid == user.uuid) {
                    failure(MatchmakingError.SamePlayer)
                }
                it.lobbyRepository.leaveLobby(lobby.user.uuid)
                val matchId = it.matchRepository.createMatch(rulesId, user.uuid)
                Matchmaker(true, matchId)
            } else {
                return it.lobbyRepository.createLobby(rulesId, user.uuid)
            }
        }
    }

    fun makeMove(game: Match, move: Move): Match {

        if (game.rules.isValidMove(game.moves, move)) {

            transactionManager.run {
                if (game.rules.isWinningMove(game.moves, move)) {
                    it.matchRepository.makeMove(move)
                    it.matchRepository.finishMatch(game.id, MatchState.FINISHED)
                } else {
                    it.matchRepository.makeMove(move)
                }
                val newGame = it.matchRepository.getMatchById(game.matchId)

            }
            val newGame = game.board.addMove(move)

        } else {
            failure(MakeMoveError.MoveIsInvalid())
        }

        return newGame
    }

    fun getLobbyInfoByUserId(userId: Int): Lobby? {
        TODO()
    }

    fun makeMove(matchId: Int, move: Move): Match {
        TODO()
    }

    fun getCurrentTurn(matchId: Int): Player? {
        TODO()
    }

    fun getMatch(matchId: Int): Match? {
        TODO()
    }



}