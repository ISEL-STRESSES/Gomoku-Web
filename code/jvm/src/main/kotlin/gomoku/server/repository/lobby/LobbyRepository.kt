package gomoku.server.repository.lobby

import gomoku.server.domain.game.Lobby
import gomoku.server.domain.game.rules.Rules
import gomoku.server.domain.user.User

/**
 * Repository for lobbies
 */
interface LobbyRepository {

    /**
     * Gets all lobbies
     * @return The lobby or null if no lobby with the given id exists
     */
    fun getLobbies(): List<Lobby>

    /**
     * Gets a lobby by its rules
     * @param rule the rule
     * @return The lobby or null if no lobby with the given id exists
     */
    fun getLobbyByRule(rule: Rules): Lobby?

    /**
     * Gets a lobby by the id of one of its players
     * @param user The id of the player
     * @return The lobby or null if no lobby with the given id exists
     */
    fun getLobbyByUser(user: User): Lobby?

    /**
     * Creates a lobby with a player (Waiting Room)
     * @param rule the rule to create a lobby with
     * @param userId The id of the user to join
     */
    fun joinLobby(rule: Rules, userId: Int) :Int

    /**
     * Removes a player from a lobby (Waiting Room)
     * @param userId The id of the user to remove
     */
    fun leaveLobby(userId: Int)
}
