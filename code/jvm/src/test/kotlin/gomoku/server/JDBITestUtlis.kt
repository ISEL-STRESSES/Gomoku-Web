package gomoku.server

import gomoku.server.repository.Transaction
import gomoku.server.repository.TransactionManager
import gomoku.server.repository.configureWithAppRequirements
import gomoku.server.repository.jdbi.JDBITransaction
import gomoku.utils.Either
import gomoku.utils.Failure
import gomoku.utils.Success
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource

private val jdbi = Jdbi.create(
    PGSimpleDataSource().apply {
        setURL(System.getenv("DB_URL"))
    }
).configureWithAppRequirements()

fun jbdiTest() = Jdbi.create(
    PGSimpleDataSource().apply {
        setURL(System.getenv("DB_URL"))
    }
).configureWithAppRequirements()

fun testWithHandleAndRollback(block: (Handle) -> Unit) = jdbi.useTransaction<Exception> { handle ->
    block(handle)
    handle.rollback()
}

fun testWithTransactionManagerAndRollback(block: (TransactionManager) -> Unit) = jdbi.useTransaction<Exception>
{ handle ->

    val transaction = JDBITransaction(handle)

    // a test TransactionManager that never commits
    val transactionManager = object : TransactionManager {
        override fun <R> run(block: (Transaction) -> R): R {
            return block(transaction)
            // n.b. no commit happens
        }
    }
    block(transactionManager)

    // finally, we roll back everything
    handle.rollback()
}

fun <L, R> Either<L, R>.successOrNull(): R? = when (this) {
    is Success -> this.value
    else -> null
}

fun <L, R> Either<L, R>.failureOrNull(): L? = when (this) {
    is Failure -> this.value
    else -> null
}
