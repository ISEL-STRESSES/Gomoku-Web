package gomoku.server.repository.lobby

import gomoku.server.domain.game.Lobby
import gomoku.server.domain.game.rules.Rules
import gomoku.server.domain.user.User

/**
 * Repository for lobbies
 */
interface LobbyRepository {

    // TODO: needed?
//    /**
//     * Creates a lobby
//     * @param lobby The lobby to create
//     * @return The id of the created lobby or null if the creation failed
//     */
//    fun makeLobby(lobby: Lobby): Int?
    // fun leaveLobby(gameRuleId: Int): Int?

    /**
     * Gets a lobby by its rules id
     * @param rule The rules of the lobby
     * @return The lobby or null if no lobby with the given id exists
     */
    fun getLobby(rule: Rules): Lobby?

    /**
     * Gets all lobbies
     * @return The lobby or null if no lobby with the given id exists
     */
    fun getLobbies(): List<Lobby>

    /**
     * Gets a lobby by the id of one of its players
     * @param userId The id of the player
     * @return The lobby or null if no lobby with the given id exists
     */
    fun getLobbyByUser(userId: Int): Lobby?

    /**
     * Gets the users present in a lobby
     * @param lobbyId The id of the lobby
     * @return The users in the lobby
     */
    fun getUsersInLobby(lobbyId: Int): List<User>

    /**
     * Gets the number of users present in a lobby
     * @param lobbyId The id of the lobby
     * @return The number of users in the lobby
     */
    fun getNrOfUsersInLobby(lobbyId: Int): Int

    /**
     * Removes a player from a lobby
     * @param lobbyId The id of the lobby
     * @param userId The id of the user to remove
     */
    fun removePlayerFromLobby(lobbyId: Int, userId: Int)

    /**
     * Joins a player to a lobby
     * @param ruleId The id of the lobby
     * @param userId The id of the user to join
     * @return The id of the lobby the user joined or null if the user could not join
     */
    fun joinLobby(ruleId: Int, userId: Int): Int
}
