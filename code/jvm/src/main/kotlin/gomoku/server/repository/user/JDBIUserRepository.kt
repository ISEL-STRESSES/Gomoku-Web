package gomoku.server.repository.user

import gomoku.server.domain.user.PasswordValidationInfo
import gomoku.server.domain.user.RankingUserData
import gomoku.server.domain.user.Token
import gomoku.server.domain.user.TokenValidationInfo
import gomoku.server.domain.user.User
import gomoku.server.domain.user.UserStats
import kotlinx.datetime.Instant
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import org.slf4j.LoggerFactory

/**
 * JDBI implementation of [UserRepository].
 * @param handle JDBI handle to the database.
 * @see UserRepository
 */
class JDBIUserRepository(private val handle: Handle) : UserRepository {

    // user
    /**
     * Stores a user in the database.
     * @param username The username of the user.
     * @param passwordValidationInfo The password validation information of the user.
     * @return The id of the user.
     */
    override fun storeUser(username: String, passwordValidationInfo: PasswordValidationInfo): Int =
        handle.createUpdate("insert into users (username, password_validation) values (:username, :password_validation)")
            .bind("username", username)
            .bind("password_validation", passwordValidationInfo.validationInfo)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .one()

    /**
     * Getter for the [User] object corresponding to the given [username].
     * @param username The username of the user to get.
     * @return The [User] object corresponding to the given [username],
     * or null if no user with this username exists.
     */
    override fun getUserByUsername(username: String): User? =
        handle.createQuery("SELECT * FROM users WHERE username = :username")
            .bind("username", username)
            .mapTo<User>()
            .singleOrNull()

    /**
     * Verifies if a user with the given [username] exists.
     * @param username The username of the user to check.
     * @return True if a user with the given [username] exists, false otherwise.
     */
    override fun isUserStoredByUsername(username: String): Boolean =
        handle.createQuery("SELECT COUNT(*) FROM users WHERE username = :username")
            .bind("username", username)
            .mapTo<Int>()
            .single() == 1

    /**
     * Verifies if a user with the given [id] exists.
     * @param id The id of the user to check.
     * @return True if a user with the given [id] exists, false otherwise.
     */
    override fun isUserStoredById(id: Int): Boolean =
        handle.createQuery("SELECT COUNT(*) FROM users WHERE id = :id")
            .bind("id", id)
            .mapTo<Int>()
            .single() == 1

    /**
     * Retrieves a user by their id.
     * @param id The id of the user.
     * @return The user if found, null otherwise.
     */
    override fun getUserById(id: Int): User? {
        return handle.createQuery("SELECT * FROM users WHERE id = :uuid")
            .bind("uuid", id)
            .mapTo<User>()
            .singleOrNull()
    }

    // token
    /**
     * Gets the [User] object corresponding to the given [tokenValidationInfo].
     * @param tokenValidationInfo The [TokenValidationInfo] object to get the [User] from.
     * @return A [Pair] containing the [User] object corresponding to the given [tokenValidationInfo],
     * and the [Token] object corresponding to the given [tokenValidationInfo], or null if no user with
     * this token exists.
     */
    override fun getTokenAndUserByTokenValidationInfo(tokenValidationInfo: TokenValidationInfo): Pair<User, Token>? =
        handle.createQuery(
            """
                select id, username, password_validation, token_validation, created_at, last_used_at
                from users 
                inner join tokens
                on users.id = tokens.user_id
                where token_validation = :validation_information
            """
        )
            .bind("validation_information", tokenValidationInfo.validationInfo)
            .mapTo<UserAndTokenModel>()
            .singleOrNull()
            ?.userAndToken

    /**
     * Creates a [Token] associated to the given [User].
     * @param token The [Token] to create.
     * @param maxTokens The maximum number of tokens allowed by [User].
     */
    override fun createToken(token: Token, maxTokens: Int) {
        val deletions = handle.createUpdate(
            """
            delete from tokens 
            where user_id = :user_id 
                and tokens.token_validation in (
                    select token_validation from tokens where user_id = :user_id 
                        order by last_used_at desc offset :offset
                )
            """.trimIndent()
        )
            .bind("user_id", token.userId)
            .bind("offset", maxTokens - 1)
            .execute()

        logger.info("{} tokens deleted when creating new token", deletions)

        handle.createUpdate(
            """
                insert into tokens(user_id, token_validation, created_at, last_used_at) 
                values (:user_id, :token_validation, :created_at, :last_used_at)
            """.trimIndent()
        )
            .bind("user_id", token.userId)
            .bind("token_validation", token.tokenValidationInfo.validationInfo)
            .bind("created_at", token.createdAt.epochSeconds)
            .bind("last_used_at", token.lastUsedAt.epochSeconds)
            .execute()
    }

    /**
     * Updates the last used date of the given [Token].
     * @param token The [Token] to update.
     * @param now The current date.
     */
    override fun updateTokenLastUsed(token: Token, now: Instant) {
        handle.createUpdate(
            """
                update tokens
                set last_used_at = :last_used
                where token_validation = :encoded_token
            """.trimIndent()
        )
            .bind("last_used", now.epochSeconds)
            .bind("encoded_token", token.tokenValidationInfo.validationInfo)
            .execute()
    }

    /**
     * Removes the given [Token].
     * @param tokenValidationInfo The [Token] to remove.
     * @return The number of tokens removed.
     */
    override fun removeTokenByTokenValidationInfo(tokenValidationInfo: TokenValidationInfo): Int =
        handle.createUpdate(
            """
                delete from tokens
                where token_validation = :encoded_token
            """.trimIndent()
        )
            .bind("encoded_token", tokenValidationInfo.validationInfo)
            .execute()

    // stats
    /**
     * Retrieves the stats of a user for every rule.
     * @param userId The id of the user.
     * @return The stats of the user for every rule, or null if the user doesn't exist.
     */
    override fun getUserStats(userId: Int): UserStats? =
        handle.createQuery(
            """
            with ranked_users as (
            select
                user_id,
                rules_id,
                elo,
                RANK() over (partition by rules_id order by elo desc) as rank
            from user_stats
        )
        select
            users.id as user_id,
            rules.id as rule_id,
            users.username,
            rules.board_size,
            rules.opening_rule,
            rules.variant,
            user_stats.games_played,
            user_stats.elo,
            ru.rank
        from users
        inner join user_stats
        on users.id = user_stats.user_id
        inner join rules
        on user_stats.rules_id = rules.id
        left join ranked_users ru
        on user_stats.user_id = ru.user_id and user_stats.rules_id = ru.rules_id
        where users.id = :user_id
            """.trimIndent()
        )
            .bind("user_id", userId)
            .mapTo<UserStats>()
            .singleOrNull()

    /**
     * Retrieves the stats of a user for a given rule.
     * @param userId The id of the user.
     * @param ruleId The id of the rule.
     * @return The stats of the user for the given rule,
     * or null if the user never played on this setting.
     */
    override fun getUserRanking(userId: Int, ruleId: Int): RankingUserData? =
        handle.createQuery(
            """
            with ranked_users as (
                select
                    user_id,
                    elo,
                    RANK() over (order by elo desc) as rank
                from user_stats
                where rules_id = :rules_id
            )
            select
                ru.user_id,
                u.username,
                us.rules_id as rule_id,
                us.games_played,
                us.elo,
                ru.rank
            from user_stats us
            join users u on us.user_id = u.id
            join ranked_users ru on us.user_id = ru.user_id
            where us.user_id = :user_id
            and us.rules_id = :rules_id
            """.trimIndent()
        )
            .bind("user_id", userId)
            .bind("rules_id", ruleId)
            .mapTo<RankingUserData>()
            .singleOrNull()

    /**
     * Sets the stats of a user for a given rule.
     * @param userId The id of the user.
     * @param rankingUserData The stats of the user for the given rule.
     */
    override fun setUserRanking(userId: Int, rankingUserData: RankingUserData) {
        handle.createUpdate(
            """
            insert into user_stats (user_id, rules_id, games_played, elo)
            values (:user_id, :rules_id, :games_played, :elo)
            on conflict (user_id, rules_id) do update
            set games_played = :games_played, elo = :elo
            """.trimIndent()
        )
            .bind("user_id", userId)
            .bind("rules_id", rankingUserData.ruleId)
            .bind("games_played", rankingUserData.gamesPlayed)
            .bind("elo", rankingUserData.elo)
            .execute()
    }

    /**
     * Retrieves a list of users with the given username.
     * @param username The username of the users.
     * @param rulesId The id of the rule.
     * @param offset The offset of the user list.
     * @param limit The limit of the user list.
     * @return The list of [UserStats] with the given username
     */
    override fun searchRanking(rulesId: Int, username: String, offset: Int, limit: Int): List<RankingUserData> =
        handle.createQuery(
            """
            with ranked_users as (
                select
                    us.user_id,
                    us.rules_id as rule_id,
                    us.games_played,
                    us.elo,
                    u.username,
                    RANK() OVER (PARTITION BY us.rules_id ORDER BY us.elo DESC) as rank
                from user_stats us
                join users u on us.user_id = u.id
                where us.rules_id = :rulesId
            )
            select
                ru.user_id,
                ru.rule_id,
                ru.games_played,
                ru.elo,
                ru.username,
                ru.rank
            from ranked_users ru
            where ru.username like :username
            order by ru.rank, ru.username asc
            limit :limit offset :offset
            """.trimIndent()
        )
            .bind("username", "%$username%")
            .bind("rulesId", rulesId)
            .bind("offset", offset)
            .bind("limit", limit)
            .mapTo<RankingUserData>()
            .list()

    /**
     * Counts the number of entries in the ranking.
     * @param rulesId The id of the rules.
     * @param username The username of the users.
     * @return The number of entries in the ranking.
     */
    override fun countRankingEntries(rulesId: Int, username: String): Int =
        handle.createQuery(
            """
            select count(*) from user_stats us
            join users u on us.user_id = u.id
            where u.username like :username and us.rules_id = :rulesId
            """.trimIndent()
        )
            .bind("username", "%$username%")
            .bind("rulesId", rulesId)
            .mapTo<Int>()
            .one()

    /**
     * Represents a user and a token.
     * @param id The id of the user.
     * @param username The username of the user.
     * @param passwordValidation The password validation information of the user.
     * @param tokenValidation The token validation information of the user.
     * @param createdAt The creation date of the token.
     * @param lastUsedAt The last used date of the token.
     */
    private data class UserAndTokenModel(
        val id: Int,
        val username: String,
        val passwordValidation: PasswordValidationInfo,
        val tokenValidation: TokenValidationInfo,
        val createdAt: Long,
        val lastUsedAt: Long
    ) {
        val userAndToken: Pair<User, Token>
            get() = Pair(
                User(
                    uuid = id,
                    username = username,
                    passwordValidationInfo = passwordValidation
                ),
                Token(
                    tokenValidationInfo = tokenValidation,
                    userId = id,
                    createdAt = Instant.fromEpochSeconds(createdAt),
                    lastUsedAt = Instant.fromEpochSeconds(lastUsedAt)
                )
            )
    }

    companion object {
        private val logger = LoggerFactory.getLogger(JDBIUserRepository::class.java)
    }
}
