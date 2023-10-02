package gomoku.server.repository.user

import gomoku.server.services.user.daos.UserDAO
import gomoku.server.services.user.daos.UserDetailsDAO

interface UserRepository {
    fun findUserById(uuid: Int): UserDAO?
    fun findUserByUsername(username: String): UserDAO?
    fun findUsers(offset: Int, limit: Int): List<UserDAO>
    fun saveUser(username: String): Int
    fun getUserDetails(uuid: Int): UserDetailsDAO?
}
