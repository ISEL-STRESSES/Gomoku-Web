package gomoku.server.repository.user.memory

import gomoku.server.repository.memory.MemoryDataSource
import gomoku.server.repository.user.UserRepository
import gomoku.server.services.user.dtos.get.UserDetailOutputDTO

class MemoryUserRepository(
    private val dataSource: MemoryDataSource
): UserRepository {
    override fun findUserById(uuid: Int): UserDetailOutputDTO? {
        TODO("Not yet implemented")
    }

    override fun findUserByUsername(username: String): UserDetailOutputDTO? {
        TODO("Not yet implemented")
    }

    override fun getRanking(offset: Int, limit: Int): List<UserDetailOutputDTO> {
        TODO("Not yet implemented")
    }

    override fun save(username: String): Int {
        TODO("Not yet implemented")
    }

}