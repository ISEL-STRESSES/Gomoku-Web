package gomoku.server.repository.jdbi.mappers

import kotlinx.datetime.Instant
import org.jdbi.v3.core.mapper.ColumnMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet
import java.sql.SQLException

/**
 * Maps a column of the result set to a [Instant]
 * @see ColumnMapper
 * @see Instant
 */
class InstantMapper : ColumnMapper<Instant> {

    /**
     * Maps a row of the result set to a [Instant]
     * @param rs The result set to map
     * @param columnNumber The column number
     * @param ctx The statement context
     * @return The [Instant] from the result set
     */
    @Throws(SQLException::class)
    override fun map(rs: ResultSet, columnNumber: Int, ctx: StatementContext): Instant =
        Instant.fromEpochSeconds(rs.getLong(columnNumber))
}
