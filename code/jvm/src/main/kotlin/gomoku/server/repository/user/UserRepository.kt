package gomoku.server.repository.user

import gomoku.server.services.user.dtos.get.UserDetailOutputDTO

interface UserRepository {
    fun findUserById(uuid: Int): UserDetailOutputDTO?
    fun findUserByUsername(username: String): UserDetailOutputDTO?
    fun getRankingList(offset: Int, limit: Int): List<UserDetailOutputDTO>
    fun save(username: String): Int
}
