package gomoku.server.repository

import gomoku.server.repository.game.GameRepository
import gomoku.server.repository.lobby.LobbyRepository
import gomoku.server.repository.user.UserRepository

/**
 * A transaction for the repositories
 */
interface Transaction {

    val usersRepository: UserRepository

    val gameRepository: GameRepository

    val lobbyRepository: LobbyRepository

    // other repository types

    /**
     * Rolls back the transaction
     */
    fun rollback()
}
