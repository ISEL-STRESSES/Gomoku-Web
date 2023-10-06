package gomoku.server.repository.jdbi

import gomoku.server.repository.Transaction
import gomoku.server.repository.user.JDBIUserRepository
import gomoku.server.repository.user.UserRepository
import org.jdbi.v3.core.Handle

class JDBITransaction(private val handle: Handle) : Transaction {

    override val usersRepository: UserRepository = JDBIUserRepository(handle)

    override fun rollback() {
        handle.rollback()
    }
}
