package gomoku.server.repository.authentication

interface AuthenticationRepository {
    fun getToken(uuid: Int): String?
    fun getPassword(uuid: Int): String?
    fun getUserID(token: String): Int?
    fun save(uuid: Int, token: String, password: String)
    fun setToken(uuid: Int, token: String)
    fun setPassword(uuid: Int, password: String)
}