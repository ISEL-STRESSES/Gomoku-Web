package gomoku.server.repository.jdbi.mappers.user

import gomoku.server.domain.user.PasswordValidationInfo
import gomoku.server.domain.user.User
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

/**
 * Maps a row of the result set to a [User]
 * @see RowMapper
 * @see User
 */
class UserRowMapper : RowMapper<User> {

    /**
     * Maps a row of the result set to a [User]
     * @param rs The result set to map
     * @param ctx The statement context
     * @return The [User] from the result set
     */
    override fun map(rs: ResultSet, ctx: StatementContext?): User {
        return User(
            uuid = rs.getInt("id"),
            username = rs.getString("username"),
            passwordValidationInfo = PasswordValidationInfo(rs.getString("password_validation"))
        )
    }
}
