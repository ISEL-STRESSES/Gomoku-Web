package gomoku.server.repository.jdbi.mappers.user

import gomoku.server.domain.user.TokenValidationInfo
import org.jdbi.v3.core.mapper.ColumnMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet
import java.sql.SQLException

/**
 * Maps a column of the result set to a [TokenValidationInfo]
 * @see ColumnMapper
 * @see TokenValidationInfo
 */
class TokenValidationInfoMapper : ColumnMapper<TokenValidationInfo> {

    /**
     * Maps a row of the result set to a [TokenValidationInfo]
     * @param r The result set to map
     * @param columnNumber The column number
     * @param ctx The statement context
     * @return The [TokenValidationInfo] from the result set
     */
    @Throws(SQLException::class)
    override fun map(r: ResultSet, columnNumber: Int, ctx: StatementContext?): TokenValidationInfo =
        TokenValidationInfo(r.getString(columnNumber))
}
