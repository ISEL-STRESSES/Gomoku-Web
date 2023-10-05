package gomoku.server.repository.user

import gomoku.server.domain.user.PasswordValidationInfo
import gomoku.server.domain.user.Token
import gomoku.server.domain.user.TokenValidationInfo
import gomoku.server.domain.user.User
import gomoku.server.domain.user.UserData
import kotlinx.datetime.Instant

interface UserRepository {
    fun storeUser(username: String, passwordValidationInfo: PasswordValidationInfo): Int
    fun getUserByUsername(username: String): User?
    fun isUserStoredByUsername(username: String): Boolean
    fun getUsersData(offset: Int, limit: Int): List<UserData>
    fun getTokenAndUserByTokenValidationInfo(tokenValidationInfo: TokenValidationInfo): Pair<User, Token>?
    fun createToken(token: Token, maxTokens: Int)
    fun updateTokenLastUsed(token: Token, now: Instant)
    fun removeTokenByTokenValidationInfo(tokenValidationInfo: TokenValidationInfo): Int
    fun getUserById(id: Int): UserData?
}
