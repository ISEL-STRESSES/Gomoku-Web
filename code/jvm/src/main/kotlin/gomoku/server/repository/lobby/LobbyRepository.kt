package gomoku.server.repository.lobby

import gomoku.server.domain.game.Lobby

/**
 * Repository for lobbies
 */
interface LobbyRepository {

    /**
     * Gets all lobbies by user id
     * @return The list of lobbies
     */
    fun getLobbiesByUserId(userId: Int): List<Lobby>

    fun getLobbiesByRuleId(userId: Int, ruleId: Int): List<Lobby>

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

    /**
     * Changes the state of a lobby
     * @param lobbyId The id of the lobby
     * @param gameId The id of the game
     * @return True if the state was changed, false otherwise
     */
    fun changeLobbySate(lobbyId: Int, gameId: Int): Boolean
}
