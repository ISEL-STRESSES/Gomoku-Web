package gomoku.server.repository

import Transaction

interface TransactionManager {
    fun <R> run(callback: (Transaction) -> R): R
}