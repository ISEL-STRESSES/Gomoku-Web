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
            SELECT lobby.id, lobby.started, lobby.game_id,rules.id as rules_id, rules.board_size, rules.variant, rules.opening_rule, users.id as user_id
            FROM lobby join users 
            on users.id = lobby.user_id
            join rules
            on lobby.rules_id = rules.id 
            where lobby.rules_id = :ruleId and lobby.started = false
            """.trimIndent()
        )
            .bind("ruleId", ruleId)
            .mapTo<Lobby>()
            .singleOrNull()

    /**
     * Gets all lobbies by rule id
     * @param ruleId The id of the rule
     * @param userId The id of the user
     * @return The list of lobbies
     */
    override fun getLobbiesByRuleId(userId: Int, ruleId: Int): List<Lobby> =
        handle.createQuery(
            """
            SELECT lobby.id, lobby.started, lobby.game_id,rules.id as rules_id, rules.board_size, rules.variant, rules.opening_rule, users.id as user_id
            FROM lobby join users 
            on users.id = lobby.user_id
            join rules
            on lobby.rules_id = rules.id 
            where lobby.rules_id = :ruleId and lobby.started = false
            """.trimIndent()
        )
            .bind("ruleId", ruleId)
            .bind("userId", userId)
            .mapTo<Lobby>()
            .list()

    /**
     * Gets all lobbies by user id
     * @param userId The id of the user
     * @return The list of lobbies
     */
    override fun getLobbiesByUserId(userId: Int): List<Lobby> =
        handle.createQuery(
            """
            SELECT lobby.id, lobby.started, lobby.game_id, rules.id as rules_id, rules.board_size, rules.variant, rules.opening_rule, lobby.user_id
            FROM lobby join rules 
            on lobby.rules_id = rules.id
            where lobby.started = false and lobby.user_id = :userId
            """.trimIndent()
        )
            .bind("userId", userId)
            .mapTo<Lobby>()
            .list()

    /**
     * Gets a lobby by its id
     * @param lobbyId The id of the lobby
     * @return The lobby or null if no lobby with the given id exists
     */
    override fun getLobbyById(lobbyId: Int): Lobby? =
        handle.createQuery(
            """
            SELECT lobby.id, lobby.started, lobby.game_id, rules.id as rules_id, rules.board_size, rules.variant, rules.opening_rule, lobby.user_id
            FROM lobby join rules
            on lobby.rules_id = rules.id 
            where lobby.id = :lobbyId
            """.trimIndent()
        )
            .bind("lobbyId", lobbyId)
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
            INSERT INTO lobby (user_id, rules_id, created_at, started) VALUES (:userId, :ruleId, :createdAt, false)
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
     * @param lobbyId The id of the lobby to remove the user from
     * @param userId The id of the user to remove
     * @return True if the user was removed, false otherwise
     */
    override fun leaveLobby(lobbyId: Int, userId: Int): Boolean {
        return handle.createUpdate(
            """
            DELETE FROM lobby WHERE id = :lobbyId AND user_id = :userId
            """.trimIndent()
        )
            .bind("lobbyId", lobbyId)
            .bind("userId", userId)
            .execute() > 0
    }

    /**
     * Changes the state of a lobby
     * @param lobbyId The id of the lobby
     * @param gameId The id of the game
     * @return True if the state was changed, false otherwise
     */
    override fun changeLobbySate(lobbyId: Int, gameId: Int): Boolean {
        return handle.createUpdate(
            """
            UPDATE lobby SET game_id = :gameId, started = true WHERE id = :lobbyId
            """.trimIndent()
        )
            .bind("lobbyId", lobbyId)
            .bind("gameId", gameId)
            .execute() > 0
    }
}
