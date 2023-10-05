package gomoku.server.repository.jdbi.mappers

import gomoku.server.domain.user.UserData
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

class UserRowMapper : RowMapper<UserData> {
    override fun map(rs: ResultSet, ctx: StatementContext): UserData {
        return UserData(
            uuid = rs.getInt("uuid"),
            username = rs.getString("username"),
            playCount = rs.getInt("play_count"),
            elo = rs.getInt("elo")
        )
    }
}
