package gomoku.server.repository.jdbi

import gomoku.server.repository.Transaction
import gomoku.server.repository.TransactionManager
import org.jdbi.v3.core.Jdbi
import org.springframework.stereotype.Component

@Component
class JDBITransactionManager(private val jdbi: Jdbi) : TransactionManager {
    override fun <R> run(block: (Transaction) -> R): R =
        jdbi.inTransaction<R, Exception> { handle ->
            val transaction = JDBITransaction(handle)
            block(transaction)
        }
}
