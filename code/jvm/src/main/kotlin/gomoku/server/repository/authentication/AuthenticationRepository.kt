package gomoku.server.repository.authentication

import gomoku.server.domain.user.Password
import gomoku.server.domain.user.Token

interface AuthenticationRepository {
    fun getToken(uuid: Int): Token?
    fun getPassword(uuid: Int): Password?
    fun getUserID(token: Token): Int?
    fun saveKeys(uuid: Int, token: Token, password: Password)
    fun setToken(uuid: Int, token: Token)
    fun setPassword(uuid: Int, password: Password)
}
