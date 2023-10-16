package gomoku.server.repository.lobby

import gomoku.server.domain.game.Lobby
import gomoku.server.domain.game.rules.Rules
import gomoku.server.domain.user.User
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import java.time.Clock

/**
 * Repository for lobbies
 * Implementation using JDBI and PostgresSQL
 * @property handle The handle to the database
 */
class JDBILobbyRepository(private val handle: Handle) : LobbyRepository {
    /**
     * Gets a lobby by its rules id
     * @param rule The rules of the lobby
     * @return The lobby or null if no lobby with the given id exists
     */
    override fun getLobbyByRule(rule: Rules): Lobby? =
        handle.createQuery(
            """
            SELECT rules.board_size, rules.variant, rules.opening_rule, 
            users.id, users.username, users.password_validation 
            FROM lobby join users 
            on users.id = lobby.user_id
            join rules
            on lobby.rules_id = rules.id 
            where lobby.rules_id = (select id from rules where 
            rules.board_size = :boardSize and 
            rules.variant = :variant and 
            rules.opening_rule = :openingRule) 
            """.trimIndent()
        )
            .bind("variant", rule.variant)
            .bind("boardSize", rule.boardSize.value)
            .bind("openingRule", rule.openingRule)
            .mapTo<Lobby>()
            .singleOrNull()

    /**
     * Gets all lobbies
     * @return The lobby or null if no lobby with the given id exists
     */
    override fun getLobbies(): List<Lobby> =
        handle.createQuery(
            """
            SELECT rules.board_size, rules.variant, rules.opening_rule, 
            users.id, users.username, users.password_validation
            FROM lobby join rules 
            on lobby.rules_id = rules.id 
            join users 
            on lobby.user_id = users.id
            """.trimIndent()
        )
            .mapTo<Lobby>()
            .list()

    /**
     * Gets a lobby by the id of one of its players
     * @param user The id of the player
     * @return The lobby or null if no lobby with the given id exists
     */
    override fun getLobbyByUser(user: User): Lobby? =
        handle.createQuery(
            """
            SELECT rules.board_size, rules.variant, rules.opening_rule, 
            users.id, users.username, users.password_validation 
            FROM lobby join users 
            on lobby.user_id = users.id 
            join rules 
            on rules.id = lobby.rules_id
            where users.id = :userId
            """.trimIndent()
        )
            .bind("userId", user)
            .mapTo<Lobby>()
            .singleOrNull()

    /**
     * Joins a player to a lobby
     * @param rule The id of the lobby
     * @param userId The id of the user to join
     * @return The id of the lobby the user joined or null if the user could not join
     */
    override fun joinLobby(rule: Rules, userId: Int): Int {
        val rulesId = handle.createQuery(
            """
            select id from rules where opening_rule = :openingRule and variant = :variant and board_size = :boardSize
            """.trimIndent()
        )
            .bind("openingRule", rule.openingRule)
            .bind("variant", rule.variant)
            .bind("boardSize", rule.boardSize.value)
            .mapTo<Int>()
            .one()

        return handle.createUpdate(
            """
            INSERT INTO lobby (user_id, rules_id, created_at) VALUES (:userId, :ruleId, :createdAt)
            """.trimIndent()
        )
            .bind("ruleId", rulesId)
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
    override fun leaveLobby(userId: Int) {
        handle.createUpdate(
            """
            DELETE FROM lobby WHERE user_id = :userId
            """.trimIndent()
        )
            .bind("userId", userId)
            .execute()
    }
}
