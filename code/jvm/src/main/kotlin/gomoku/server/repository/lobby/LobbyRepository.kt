package gomoku.server.repository.lobby

import gomoku.server.domain.game.Lobby
import gomoku.server.domain.game.rules.Rules
import gomoku.server.domain.user.User

/**
 * Repository for lobbies
 */
interface LobbyRepository {

    /**
     * Gets a lobby by its rules
     * @param rule The rules of the lobby
     * @return The lobby or null if no lobby with the given id exists
     */
    fun getLobbyByRules(rule: Rules): Lobby?

    /**
     * Gets all lobbies
     * @return The lobby or null if no lobby with the given id exists
     */
    fun getLobbies(): List<Lobby>

    /**
     * Gets a lobby by the id of one of its players
     * @param user The id of the player
     * @return The lobby or null if no lobby with the given id exists
     */
    fun getLobbyByUser(user: User): Lobby?

    /**
     * Creates a lobby with a player (Waiting Room)
     * @param rule The rule to create a lobby with
     * @param userId The id of the user to join
     */
    fun createLobby(rule: Rules, userId: Int)

    /**
     * Removes a player from a lobby (Waiting Room)
     * @param userId The id of the user to remove
     */
    fun leaveLobby(userId: Int)
}
