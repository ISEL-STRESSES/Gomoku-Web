package gomoku.server.services.lobby

import gomoku.server.domain.game.Lobby
import gomoku.server.domain.game.Matchmaker
import gomoku.server.http.controllers.lobby.models.LeaveLobbyOutput
import gomoku.server.repository.TransactionManager
import gomoku.server.services.errors.game.MatchmakingError
import gomoku.server.services.errors.lobby.GetLobbyError
import gomoku.server.services.errors.lobby.JoinLobbyError
import gomoku.server.services.errors.lobby.LeaveLobbyError
import gomoku.server.services.game.MatchmakingResult
import gomoku.utils.failure
import gomoku.utils.success
import org.springframework.stereotype.Service
import kotlin.random.Random

@Service
class LobbyService(private val transactionManager: TransactionManager) {

    /**
     * Starts the matchmaking process for the given rule and user.
     * @param ruleId id of the rule
     * @param userId the id of the user that wants to play
     * @return the result of the matchmaking process
     * @see MatchmakingResult
     */
    fun startMatchmakingProcess(ruleId: Int, userId: Int): MatchmakingResult {
        return transactionManager.run { transaction ->
            val lobbies = transaction.lobbyRepository.getLobbiesByRuleId(userId, ruleId)

            if (lobbies.isNotEmpty()) {
                if (lobbies.any { it.userId == userId }) {
                    return@run failure(MatchmakingError.SamePlayer)
                }
                val lobby = lobbies.firstOrNull() ?: return@run failure(MatchmakingError.LobbyNotFound)
                val playerBlack = if (Random.nextBoolean()) userId else lobby.userId
                val playerWhite = if (playerBlack == userId) lobby.userId else userId
                val gameId = transaction.gameRepository.createGame(ruleId, playerBlack, playerWhite)
                val stateChanged = transaction.lobbyRepository.changeLobbySate(lobby.id, gameId)
                if (!stateChanged) {
                    return@run failure(MatchmakingError.LobbySateChangeFailed)
                }
                return@run success(Matchmaker(true, gameId))
            } else {
                return@run success(Matchmaker(false, transaction.lobbyRepository.createLobby(ruleId, userId)))
            }
        }
    }

    /**
     * Leaves the matchmaking process.
     * @param userId id of the user
     * @return a LeaveLobbyOutput in case of success or a LeaveLobbyError in case of a failure
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

    /**
     * Creates a Lobby with a user in it with a specific rule.
     * @param ruleId the rule to apply to the lobby.
     * @param userId to be a part of the lobby
     * @return A MatchMaker with the id of the lobby.
     */
    fun createLobby(ruleId: Int, userId: Int): Matchmaker =
        transactionManager.run {
            Matchmaker(false, it.lobbyRepository.createLobby(ruleId, userId))
        }

    /**
     * Joins a user to a lobby
     * @param lobbyId the id of the lobby
     * @param userId the id of the lobby
     * @return The Result of the joining a lobby operation
     */
    fun joinLobby(lobbyId: Int, userId: Int): JoinLobbyResult =
        transactionManager.run {
            val lobbyGet = it.lobbyRepository.getLobbyById(lobbyId)
                ?: return@run failure(JoinLobbyError.LobbyNotFound)
            if (lobbyGet.userId == userId) {
                return@run failure(JoinLobbyError.UserAlreadyInLobby)
            }
            val playerBlack = if (Random.nextBoolean()) userId else lobbyGet.userId
            val playerWhite = if (playerBlack == userId) lobbyGet.userId else userId
            val gameId = it.gameRepository.createGame(lobbyGet.rule.ruleId, playerBlack, playerWhite)
            val stateChanged = it.lobbyRepository.changeLobbySate(lobbyGet.id, gameId)
            if (!stateChanged) {
                return@run failure(JoinLobbyError.LobbySateChangeFailed)
            }
            return@run success(Matchmaker(true, gameId))
        }

    /**
     * Gets all the lobbies.
     * @return List of lobbies.
     */
    fun getLobbiesByUserId(userId: Int): List<Lobby> =
        transactionManager.run {
            it.lobbyRepository.getLobbiesByUserId(userId)
        }

    /**
     * Searches for a lobby by the given [lobbyId].
     * @param lobbyId id to search
     * @return Either returns a Lobby if successful or an [GetLobbyError].
     */
    fun getLobbyById(lobbyId: Int): GetLobbyResult =
        transactionManager.run {
            val lobby = it.lobbyRepository.getLobbyById(lobbyId) ?: return@run failure(GetLobbyError.LobbyNotFound)
            return@run success(lobby)
        }
}
