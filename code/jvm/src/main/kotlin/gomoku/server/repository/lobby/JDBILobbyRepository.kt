package gomoku.server.repository.lobby

import gomoku.server.domain.game.Lobby
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import java.time.Clock

/**
 * Repository for lobbies
 * Implementation using JDBI and PostgresSQL
 * @property handle The handle to the database
 * @see LobbyRepository
 */
class JDBILobbyRepository(private val handle: Handle) : LobbyRepository {
    /**
     * Gets a lobby by its rules id
     * @param ruleId the rule id of the lobby
     * @return The lobby or null if no lobby with the given rule id exists
     */
    override fun getLobbyByRuleId(ruleId: Int): Lobby? =
        handle.createQuery(
            """
            SELECT lobby.id, rules.id as rules_id, rules.board_size, rules.variant, rules.opening_rule, users.id as user_id
            FROM lobby join users 
            on users.id = lobby.user_id
            join rules
            on lobby.rules_id = rules.id 
            where lobby.rules_id = :ruleId
            """.trimIndent()
        )
            .bind("ruleId", ruleId)
            .mapTo<Lobby>()
            .singleOrNull()

    /**
     * Gets all lobbies
     * @return The list of lobbies
     */
    override fun getLobbies(): List<Lobby> =
        handle.createQuery(
            """
            SELECT lobby.id, rules.id as rules_id, rules.board_size, rules.variant, rules.opening_rule, lobby.user_id
            FROM lobby join rules 
            on lobby.rules_id = rules.id 
            """.trimIndent()
        )
            .mapTo<Lobby>()
            .list()

    /**
     * Gets a lobby by the id of one of its players
     * @param userId The id of the player
     * @return The lobby or null if no lobby with the given id exists
     */
    override fun getLobbyByUserId(userId: Int): Lobby? =
        handle.createQuery(
            """
            SELECT rules.id as rules_id, rules.board_size, rules.variant, rules.opening_rule, lobby.user_id            
            FROM lobby join rules 
            on rules.id = lobby.rules_id
            where lobby.user_id= :userId
            """.trimIndent()
        )
            .bind("userId", userId)
            .mapTo<Lobby>()
            .singleOrNull()

    /**
     * Creates a lobby with a player
     * @param ruleId the rule id to create a lobby with
     * @param userId The id of the user to join
     * @return The id of the lobby the user joined
     */
    override fun createLobby(ruleId: Int, userId: Int): Int {
        return handle.createUpdate(
            """
            INSERT INTO lobby (user_id, rules_id, created_at) VALUES (:userId, :ruleId, :createdAt)
            """.trimIndent()
        )
            .bind("ruleId", ruleId)
            .bind("userId", userId)
            .bind("createdAt", Clock.systemUTC().instant().epochSecond)
            .executeAndReturnGeneratedKeys("id")
            .mapTo<Int>()
            .one()
    }

    /**
     * Removes a player from a lobby (Waiting Room)
     * @param userId The id of the user to remove
     */
    override fun leaveLobby(userId: Int): Boolean {
        return handle.createUpdate(
            """
            DELETE FROM lobby WHERE user_id = :userId
            """.trimIndent()
        )
            .bind("userId", userId)
            .execute() > 0
    }
}
