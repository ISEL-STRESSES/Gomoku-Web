package gomoku.server.repository.user

import gomoku.server.services.user.dtos.get.UserDetailOutputDTO
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

class UserRowMapper : RowMapper<UserDetailOutputDTO> {
    override fun mapRow(rs: ResultSet, rowNum: Int): UserDetailOutputDTO {
        return UserDetailOutputDTO(
            uuid = rs.getInt("uuid"),
            username = rs.getString("username"),
            playCount = rs.getInt("play_count"),
            elo = rs.getInt("elo")
        )
    }
}
