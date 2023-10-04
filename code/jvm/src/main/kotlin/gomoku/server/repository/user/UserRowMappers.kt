package gomoku.server.repository.user

import gomoku.server.domain.user.UserExternalInfo
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

class UserRowMappers : RowMapper<UserExternalInfo> {
    override fun mapRow(rs: ResultSet, rowNum: Int): UserExternalInfo {
        return UserExternalInfo(
            uuid = rs.getInt("uuid"),
            username = rs.getString("username"),
            playCount = rs.getInt("play_count"),
            elo = rs.getInt("elo")
        )
    }
}
