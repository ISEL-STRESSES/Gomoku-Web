package gomoku.server.repository.authentication

import org.springframework.stereotype.Repository

@Repository
class JDBCAuthenticationRepository : AuthenticationRepository {
    override fun getToken(uuid: Int): String {
        TODO("Not yet implemented")
    }

    override fun getPassword(uuid: Int): String {
        TODO("Not yet implemented")
    }

    override fun getUserID(token: String): Int {
        TODO("Not yet implemented")
    }

    override fun save(uuid: Int, token: String, password: String) {
        TODO("Not yet implemented")
    }

    override fun setToken(uuid: Int, token: String) {
        TODO("Not yet implemented")
    }

    override fun setPassword(uuid: Int, password: String) {
        TODO("Not yet implemented")
    }
}