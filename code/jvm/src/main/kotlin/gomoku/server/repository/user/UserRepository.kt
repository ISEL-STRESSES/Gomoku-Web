package gomoku.server.repository.user

import gomoku.server.services.user.dtos.get.UserDetailOutputDTO

interface UserRepository {
    fun existsUserWithUsername(username: String): Boolean
    fun findUserById(uuid: Int): UserDetailOutputDTO?
    fun findUserByUsername(username: String): UserDetailOutputDTO?
    fun findUsers(offset: Int, limit: Int): List<UserDetailOutputDTO>
    fun save(username: String): Int
}
