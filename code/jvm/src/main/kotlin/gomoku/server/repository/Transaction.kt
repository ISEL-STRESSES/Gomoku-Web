package gomoku.server.repository

import gomoku.server.repository.user.UserRepository

interface Transaction {

    val usersRepository: UserRepository

    // other repository types
    fun rollback()
}