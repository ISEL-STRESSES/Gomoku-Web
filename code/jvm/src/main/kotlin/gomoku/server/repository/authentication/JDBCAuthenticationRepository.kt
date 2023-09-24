package gomoku.server.repository.authentication

import gomoku.server.domain.user.Password
import gomoku.server.domain.user.Token
import org.springframework.stereotype.Repository

@Repository
class JDBCAuthenticationRepository : AuthenticationRepository {
    override fun getToken(uuid: Int): Token? {
        TODO("Not yet implemented")
    }

    override fun getPassword(uuid: Int): Password? {
        TODO("Not yet implemented")
    }

    override fun getUserID(token: Token): Int? {
        TODO("Not yet implemented")
    }

    override fun save(uuid: Int, token: Token, password: Password) {
        TODO("Not yet implemented")
    }

    override fun setToken(uuid: Int, token: Token) {
        TODO("Not yet implemented")
    }

    override fun setPassword(uuid: Int, password: Password) {
        TODO("Not yet implemented")
    }
}