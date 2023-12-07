package gomoku.server.repository.lobby

import gomoku.server.domain.game.Lobby

/**
 * Repository for lobbies
 */
interface LobbyRepository {

    /**
     * Gets all lobbies
     * @return The list of lobbies
     */
    fun getLobbies(): List<Lobby>

    /**
     * Gets a lobby by its id
     * @param lobbyId The id of the lobby
     * @return The lobby or null if no lobby with the given id exists
     */
    fun getLobbyById(lobbyId: Int): Lobby?

    /**
     * Gets a lobby by its rules id
     * @param ruleId the rule id of the lobby
     * @return The lobby or null if no lobby with the given rule id exists
     */
    fun getLobbyByRuleId(ruleId: Int): Lobby?

    /**
     * Gets a lobby by the id of one of its players
     * @param userId The id of the player
     * @return The lobby or null if no lobby with the given id exists
     */
    fun getLobbyByUserId(userId: Int): Lobby?

    /**
     * Creates a lobby with a player
     * @param ruleId the rule id to create a lobby with
     * @param userId The id of the user to join
     * @return The id of the lobby the user joined
     */
    fun createLobby(ruleId: Int, userId: Int): Int

    /**
     * Removes a player from a lobby (Waiting Room)
     * @param userId The id of the user to remove
     */
    fun leaveLobby(lobbyId: Int, userId: Int): Boolean

    fun changeLobbySate(lobbyId: Int, gameId: Int): Boolean
}
