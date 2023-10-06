package gomoku.server.repository.user

import gomoku.server.domain.user.PasswordValidationInfo
import gomoku.server.domain.user.Token
import gomoku.server.domain.user.TokenValidationInfo
import gomoku.server.domain.user.User
import gomoku.server.domain.user.UserData
import kotlinx.datetime.Instant
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import org.slf4j.LoggerFactory

class JDBIUserRepository(private val handle: Handle) : UserRepository {
    override fun getUserByUsername(username: String): User? =
        handle.createQuery("SELECT * FROM users WHERE username = :username")
            .bind("username", username)
            .mapTo<User>()
            .singleOrNull()

    override fun isUserStoredByUsername(username: String): Boolean =
        handle.createQuery("SELECT COUNT(*) FROM users WHERE username = :username")
            .bind("username", username)
            .mapTo<Int>()
            .single() == 1

    override fun getUsersData(offset: Int, limit: Int): List<UserData> = // TODO: VER COMO O MATOS FEZ NO TRABALHO DELE
        handle.createQuery("SELECT * FROM users LIMIT :limit OFFSET :offset")
            .bind("limit", limit)
            .bind("offset", offset)
            .mapTo<UserData>()
            .list()

    override fun getTokenAndUserByTokenValidationInfo(tokenValidationInfo: TokenValidationInfo): Pair<User, Token>? =
        handle.createQuery(
            """
                select id, username, password_validation, games_played, elo, token_validation, created_at, last_used
                from users as player 
                inner join tokens as tokens 
                on player.id = tokens.user_id
                where token_validation = :validation_information
            """
        )
            .bind("validation_information", tokenValidationInfo.validationInfo)
            .mapTo<UserAndTokenModel>()
            .singleOrNull()
            ?.userAndToken

    override fun createToken(token: Token, maxTokens: Int) {
        val deletions = handle.createUpdate(
            """
            delete from tokens 
            where user_id = :user_id 
                and tokens.token_validation in (
                    select token_validation from tokens where user_id = :user_id 
                        order by last_used desc offset :offset
                )
            """.trimIndent()
        )
            .bind("user_id", token.userId)
            .bind("offset", maxTokens - 1)
            .execute()

        logger.info("{} tokens deleted when creating new token", deletions)

        handle.createUpdate(
            """
                insert into tokens(user_id, token_validation, created_at, last_used) 
                values (:user_id, :token_validation, :created_at, :last_used_at)
            """.trimIndent()
        )
            .bind("user_id", token.userId)
            .bind("token_validation", token.tokenValidationInfo.validationInfo)
            .bind("create_date", token.createdAt.epochSeconds)
            .bind("last_used", token.lastUsedAt.epochSeconds)
            .execute()
    }

    override fun updateTokenLastUsed(token: Token, now: Instant) {
        handle.createUpdate(
            """
                update tokens
                set last_used = :last_used
                where token_validation = :encoded_token
            """.trimIndent()
        )
            .bind("last_used", now.epochSeconds)
            .bind("encoded_token", token.tokenValidationInfo.validationInfo)
            .execute()
    }

    override fun removeTokenByTokenValidationInfo(tokenValidationInfo: TokenValidationInfo): Int {
        return handle.createUpdate(
            """
                delete from tokens
                where token_validation = :encoded_token
            """
        )
            .bind("encoded_token", tokenValidationInfo.validationInfo)
            .execute()
    }

    override fun getUserById(id: Int): UserData? {
        return handle.createQuery("SELECT * FROM users WHERE id = :uuid")
            .bind("uuid", id)
            .mapTo<UserData>()
            .singleOrNull()
    }

    override fun storeUser(username: String, passwordValidationInfo: PasswordValidationInfo): Int =
        handle.createUpdate("insert into users (username, password_validation) values (:username, :password_validation)")
            .bind("username", username)
            .bind("encoded_password", passwordValidationInfo.validationInfo)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .one()

    private data class UserAndTokenModel(
        val id: Int,
        val username: String,
        val passwordValidation: PasswordValidationInfo,
        val playCount: Int,
        val elo: Int,
        val tokenValidation: TokenValidationInfo,
        val createdAt: Long,
        val lastUsedAt: Long
    ) {
        val userAndToken: Pair<User, Token>
            get() = Pair(
                User(id, username, playCount, elo, passwordValidation),
                Token(
                    tokenValidation,
                    id,
                    Instant.fromEpochSeconds(createdAt),
                    Instant.fromEpochSeconds(lastUsedAt)
                )
            )
    }

    companion object {
        private val logger = LoggerFactory.getLogger(JDBIUserRepository::class.java)
    }
}
