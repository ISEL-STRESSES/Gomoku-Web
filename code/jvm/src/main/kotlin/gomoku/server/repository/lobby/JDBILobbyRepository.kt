package gomoku.server.repository.lobby

import gomoku.server.domain.game.Lobby
import gomoku.server.domain.game.rules.Rules
import gomoku.server.domain.user.User
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

class JDBILobbyRepository(private val handle: Handle) : LobbyRepository {
    /**
     * Gets a lobby by its rules id
     * @param rule The rules of the lobby
     * @return The lobby or null if no lobby with the given id exists
     */
    override fun getLobby(rule: Rules): Lobby? =
        handle.createQuery(
            """
            SELECT * FROM lobby WHERE lobby.rules_id = (
            select id from rules where variant = :variant and board_size = :boardSize and opening_rule = :openingRule
            )
            """.trimIndent()
        )
            .bind("variant", rule.variant)
            .bind("boardSize", rule.boardSize.value)
            .bind("openingRule", rule.openingRule)
            .mapToBean(Lobby::class.java)
            .findFirst()
            .orElse(null)

    /**
     * Gets all lobbies
     * @return The lobby or null if no lobby with the given id exists
     */
    override fun getLobbies(): List<Lobby> =
        handle.createQuery("SELECT * FROM lobby")
            .mapTo<Lobby>()
            .list()

    /**
     * Gets a lobby by the id of one of its players
     * @param userId The id of the player
     * @return The lobby or null if no lobby with the given id exists
     */
    override fun getLobbyByUser(userId: Int): Lobby? =
        handle.createQuery(
            """
            SELECT * FROM lobby WHERE user_id = :userId
            """.trimIndent()
        )
            .bind("userId", userId)
            .mapTo<Lobby>()
            .singleOrNull()

    /**
     * Gets the users present in a lobby
     * @param lobbyId The id of the lobby
     * @return The users in the lobby
     */
    override fun getUsersInLobby(lobbyId: Int): List<User> =
        handle.createQuery(
            """
            SELECT * FROM users WHERE id IN (
            SELECT user_id FROM lobby WHERE id = :lobbyId
            )
            """.trimIndent()
        )
            .bind("lobbyId", lobbyId)
            .mapTo<User>()
            .list()

    /**
     * Gets the number of users present in a lobby
     * @param lobbyId The id of the lobby
     * @return The number of users in the lobby
     */
    override fun getNrOfUsersInLobby(lobbyId: Int): Int = getUsersInLobby(lobbyId).size

    /**
     * Removes a player from a lobby
     * @param lobbyId The id of the lobby
     * @param userId The id of the user to remove
     */
    override fun removePlayerFromLobby(lobbyId: Int, userId: Int) {
        handle.createUpdate(
            """
            DELETE FROM lobby WHERE id = :lobbyId AND user_id = :userId
            """.trimIndent()
        )
            .bind("lobbyId", lobbyId)
            .bind("userId", userId)
            .execute()
    }

    /**
     * Joins a player to a lobby
     * @param ruleId The id of the lobby
     * @param userId The id of the user to join
     * @return The id of the lobby the user joined or null if the user could not join
     */
    override fun joinLobby(ruleId: Int, userId: Int): Int {
        val lobbyId = handle.createUpdate(
            """
            INSERT INTO lobby (user_id, rules_id, created_at) VALUES (:userId, :ruleId, :createdAt)
            """.trimIndent()
        )
            .bind("ruleId", ruleId)
            .bind("userId", userId)
            .bind("createdAt", System.currentTimeMillis())
            .executeAndReturnGeneratedKeys("id")
            .mapTo<Int>()
            .firstOrNull()
        return lobbyId ?: -1
    }
}
