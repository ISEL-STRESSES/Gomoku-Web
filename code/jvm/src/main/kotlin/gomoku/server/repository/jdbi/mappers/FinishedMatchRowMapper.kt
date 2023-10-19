package gomoku.server.repository.jdbi.mappers

import gomoku.server.domain.game.match.FinishedMatch
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

/**
 * Maps a row of the result set to a [FinishedMatch]
 * @see RowMapper
 * @see FinishedMatch
 */
class FinishedMatchRowMapper : RowMapper<FinishedMatch> {

    /**
     * Maps a row of the result set to a [FinishedMatch]
     * @param rs The result set to map
     * @param ctx The statement context
     * @return The [FinishedMatch] from the result set
     */
    override fun map(rs: ResultSet, ctx: StatementContext): FinishedMatch {
        val moveContainer = getMoveContainerFromRS(rs)

        val matchState = rs.getString("match_state").uppercase()
        if (matchState != "FINISHED") {
            throw IllegalArgumentException("Expected a finished match, but found $matchState state")
        }

        return getFinishedMatchFromRS(rs, moveContainer)
    }
}
