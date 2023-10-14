package gomoku.server.repository.jdbi.mappers

import gomoku.server.domain.game.Lobby
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

class LobbyRowMapper : RowMapper<Lobby> {
    override fun map(rs: ResultSet, ctx: StatementContext?): Lobby {
        TODO()
    }
}
