package gomoku.server.repository

import gomoku.server.repository.match.MatchRepository
import gomoku.server.repository.lobby.LobbyRepository
import gomoku.server.repository.user.UserRepository

interface Transaction {

    val usersRepository: UserRepository

    val matchRepository: MatchRepository

    val lobbyRepository: LobbyRepository

    // other repository types
    fun rollback()
}
