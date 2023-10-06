package gomoku.server.repository

import gomoku.server.repository.game.MatchRepository
import gomoku.server.repository.user.UserRepository

interface Transaction {

    val usersRepository: UserRepository

    val matchRepository: MatchRepository

    // other repository types
    fun rollback()
}
