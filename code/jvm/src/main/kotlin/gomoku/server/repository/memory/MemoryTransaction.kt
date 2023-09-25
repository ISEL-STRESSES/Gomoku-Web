package gomoku.server.repository.memory

import Transaction
import gomoku.server.repository.authentication.AuthenticationRepository
import gomoku.server.repository.authentication.memory.MemoryAuthenticationRepository
import gomoku.server.repository.user.memory.MemoryUserRepository

class MemoryTransaction(
    dataSource: MemoryDataSource
) : Transaction {

    override val userRepository = MemoryUserRepository(dataSource)
    override val authenticationRepository = MemoryAuthenticationRepository(dataSource)

    override fun rollback() {
        TODO("Rollback not possible on memory transaction")
    }

}
