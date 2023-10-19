package gomoku.server.repository.jdbi

import gomoku.server.repository.Transaction
import gomoku.server.repository.lobby.JDBILobbyRepository
import gomoku.server.repository.lobby.LobbyRepository
import gomoku.server.repository.match.JDBIMatchRepository
import gomoku.server.repository.match.MatchRepository
import gomoku.server.repository.user.JDBIUserRepository
import gomoku.server.repository.user.UserRepository
import org.jdbi.v3.core.Handle

/**
 * A JDBI implementation of [Transaction]
 * @see Transaction
 * @see Handle
 */
class JDBITransaction(private val handle: Handle) : Transaction {

    override val usersRepository: UserRepository = JDBIUserRepository(handle)

    override val matchRepository: MatchRepository = JDBIMatchRepository(handle)

    override val lobbyRepository: LobbyRepository = JDBILobbyRepository(handle)

    /**
     * Rolls back the transaction
     */
    override fun rollback() {
        handle.rollback()
    }
}
