package gomoku.server.repository.jdbi

import gomoku.server.repository.Transaction
import gomoku.server.repository.game.JDBIMatchRepository
import gomoku.server.repository.game.MatchRepository
import gomoku.server.repository.lobby.JDBILobbyRepository
import gomoku.server.repository.lobby.LobbyRepository
import gomoku.server.repository.user.JDBIUserRepository
import gomoku.server.repository.user.UserRepository
import org.jdbi.v3.core.Handle

class JDBITransaction(private val handle: Handle) : Transaction {

    override val usersRepository: UserRepository = JDBIUserRepository(handle)

    override val matchRepository: MatchRepository = JDBIMatchRepository(handle)

    override val lobbyRepository: LobbyRepository = JDBILobbyRepository(handle)

    override fun rollback() {
        handle.rollback()
    }
}
