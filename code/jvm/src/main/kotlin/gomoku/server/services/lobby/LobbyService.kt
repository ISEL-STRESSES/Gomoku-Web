package gomoku.server.services.lobby

import gomoku.server.domain.game.Lobby
import gomoku.server.domain.game.Matchmaker
import gomoku.server.http.controllers.lobby.models.LeaveLobbyOutput
import gomoku.server.repository.TransactionManager
import gomoku.server.services.errors.lobby.GetLobbyError
import gomoku.server.services.errors.lobby.JoinLobbyError
import gomoku.server.services.errors.lobby.LeaveLobbyError
import gomoku.utils.failure
import gomoku.utils.success
import org.springframework.stereotype.Service
import kotlin.random.Random

@Service
class LobbyService(private val transactionManager: TransactionManager) {
    /**
     * Leaves the matchmaking process.
     * @param userId id of the user
     * @return an empty [Success] if the user left the matchmaking process, a [Failure] otherwise
     */
    fun leaveLobby(lobbyId: Int, userId: Int): LeaveLobbyResult =
        transactionManager.run {
            val lobby = it.lobbyRepository.getLobbyById(lobbyId)
                ?: return@run failure(LeaveLobbyError.LobbyNotFound)

            if (lobby.userId != userId) return@run failure(LeaveLobbyError.UserNotInLobby)

            if (it.lobbyRepository.leaveLobby(lobby.id, lobby.userId)) {
                return@run success(LeaveLobbyOutput(lobbyId, userId))
            } else {
                return@run failure(LeaveLobbyError.LeaveLobbyFailed)
            }
        }

    fun createLobby(ruleId: Int, userId: Int): Matchmaker =
        transactionManager.run {
            Matchmaker(false, it.lobbyRepository.createLobby(ruleId, userId))
        }

    fun joinLobby(lobbyId: Int, userId: Int): JoinLobbyResult =
        transactionManager.run {
            val lobbyGet = it.lobbyRepository.getLobbyById(lobbyId)
                ?: return@run failure(JoinLobbyError.LobbyNotFound)
            if (lobbyGet.userId == userId) {
                return@run failure(JoinLobbyError.UserAlreadyInLobby)
            }
            val playerBlack = if (Random.nextBoolean()) userId else lobbyGet.userId
            val playerWhite = if (playerBlack == userId) lobbyGet.userId else userId
            val didLeave = it.lobbyRepository.leaveLobby(lobbyGet.id, lobbyGet.userId)
            if (!didLeave) {
                return@run failure(JoinLobbyError.JoinLobbyFailed)
            }
            val gameId = it.gameRepository.createGame(lobbyGet.rule.ruleId, playerBlack, playerWhite)
            return@run success(Matchmaker(true, gameId))
        }

    fun getLobbies(): List<Lobby> =
        transactionManager.run {
            it.lobbyRepository.getLobbies()
        }

    fun getLobbyById(lobbyId: Int): GetLobbyResult =
        transactionManager.run {
            val lobby = it.lobbyRepository.getLobbyById(lobbyId) ?: return@run failure(GetLobbyError.LobbyNotFound)
            return@run success(lobby)
        }
}
