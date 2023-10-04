package gomoku.server.repository.user

import gomoku.server.domain.user.PasswordValidationInfo
import gomoku.server.domain.user.Token
import gomoku.server.domain.user.TokenValidationInfo
import gomoku.server.domain.user.User
import gomoku.server.domain.user.UserExternalInfo
import kotlinx.datetime.Instant
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import org.springframework.stereotype.Repository

@Repository
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

    override fun findUsersExternalInfo(offset: Int, limit: Int): List<UserExternalInfo> = // TODO: VER COMO O MATOS FEZ NO TRABALHO DELE
        handle.createQuery("SELECT * FROM users LIMIT :limit OFFSET :offset")
            .bind("limit", limit)
            .bind("offset", offset)
            .mapTo<UserExternalInfo>()
            .list()

    override fun getTokenByTokenValidationInfo(tokenValidationInfo: TokenValidationInfo): Pair<User, Token>? =
        handle.createQuery(
            """
                select uuid, username, encoded_password, games_played, elo, encoded_token, create_date, last_used
                from users as player 
                inner join token as tokens 
                on player.uuid = tokens.user_id
                where encoded_token = :validation_information
            """
        )
            .bind("validation_information", tokenValidationInfo.validationInfo)
            .mapTo<UserAndTokenModel>()
            .singleOrNull()
            ?.userAndToken

    override fun createToken(token: Token, maxTokens: Int) {
        val deletions = handle.createUpdate(
            """
            delete from token 
            where user_id = :user_id 
                and token.encoded_token in (
                    select encoded_token from token where user_id = :user_id 
                        order by last_used desc offset :offset
                )
            """.trimIndent()
        )
            .bind("user_id", token.userId)
            .bind("offset", maxTokens - 1)
            .execute()

        handle.createUpdate(
            """
                insert into token(user_id, encoded_token, create_date, last_used, ttl) 
                values (:user_id, :token_validation, :created_at, :last_used_at, :ttl)
            """.trimIndent()
        )
            .bind("user_id", token.userId)
            .bind("token_validation", token.tokenValidationInfo.validationInfo)
            .bind("create_date", token.createdAt.epochSeconds)
            .bind("last_used", token.lastUsedAt.epochSeconds)
            .bind("ttl", token.ttl)
            .execute()
    }

    override fun updateTokenLastUsed(token: Token, now: Instant) {
        handle.createUpdate(
            """
                update token
                set last_used = :last_used
                where encoded_token = :encoded_token
            """.trimIndent()
        )
            .bind("last_used", now.epochSeconds)
            .bind("encoded_token", token.tokenValidationInfo.validationInfo)
            .execute()
    }

    override fun removeTokenByTokenValidationInfo(tokenValidationInfo: TokenValidationInfo): Int {
        return handle.createUpdate(
            """
                delete from token
                where encoded_token = :encoded_token
            """
        )
            .bind("encoded_token", tokenValidationInfo.validationInfo)
            .execute()
    }

    override fun getUserById(id: Int): User? {
        return handle.createQuery("SELECT * FROM users WHERE uuid = :uuid")
            .bind("uuid", id)
            .mapTo<User>()
            .singleOrNull()
    }

    override fun storeUser(username: String, passwordValidationInfo: PasswordValidationInfo): Int =
        handle.createUpdate("insert into users (username, encoded_password) values (:username, :encoded_password)")
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
}
