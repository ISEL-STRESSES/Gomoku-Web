package gomoku.server.repository.jdbi.mappers

import gomoku.server.domain.user.PasswordValidationInfo
import gomoku.server.domain.user.User
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

class UserRowMapper : RowMapper<User> {
    override fun map(rs: ResultSet, ctx: StatementContext?): User {
        return User(
            uuid = rs.getInt("id"),
            username = rs.getString("username"),
            passwordValidationInfo = PasswordValidationInfo(rs.getString("password_validation"))
        )
    }
}
