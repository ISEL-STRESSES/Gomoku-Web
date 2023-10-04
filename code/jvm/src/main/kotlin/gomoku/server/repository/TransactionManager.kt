package gomoku.server.repository

interface TransactionManager {
    fun <R> run(block: (Transaction) -> R): R
}
