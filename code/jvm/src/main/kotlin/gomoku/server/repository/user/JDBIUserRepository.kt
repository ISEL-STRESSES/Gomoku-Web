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

    override fun findUsersExternalInfo(offset: Int, limit: Int): List<UserExternalInfo> = //TODO: VER COMO O MATOS FEZ NO TRABALHO DELE
        handle.createQuery("SELECT * FROM users LIMIT :limit OFFSET :offset")
            .bind("limit", limit)
            .bind("offset", offset)
            .mapTo<UserExternalInfo>()
            .list()

    override fun getTokenByTokenValidationInfo(tokenValidationInfo: TokenValidationInfo): Pair<User, Token>? {
        TODO("Not yet implemented")
    }

    override fun createToken(token: Token, maxTokens: Int) {
        TODO("Not yet implemented")
    }

    override fun updateTokenLastUsed(token: Token, now: Instant) {
        TODO("Not yet implemented")
    }

    override fun removeTokenByTokenValidationInfo(tokenValidationInfo: TokenValidationInfo): Int {
        TODO("Not yet implemented")
    }

    override fun storeUser(username: String, passwordValidationInfo: PasswordValidationInfo): Int = //TODO: ADD PASSWORD VALIDATION INFO TO QUERY
        handle.createUpdate("insert into users (username) values (:username)")
            .bind("username", username)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .one()

}
