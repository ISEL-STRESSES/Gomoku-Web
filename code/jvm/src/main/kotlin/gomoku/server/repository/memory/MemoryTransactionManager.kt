package gomoku.server.repository.memory

import Transaction
import gomoku.server.repository.TransactionManager
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class MemoryTransactionManager(
    private val dataSource: MemoryDataSource
) : TransactionManager {

    val lock = ReentrantLock()

    override fun <R> run(callback: (Transaction) -> R): R {
        lock.withLock {
            val transaction = MemoryTransaction(dataSource)

            // Note: Rollback is not possible on memory transaction
            return@run callback(transaction)
        }
    }
}
