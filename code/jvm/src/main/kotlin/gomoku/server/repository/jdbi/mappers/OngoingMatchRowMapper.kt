package gomoku.server.repository.jdbi.mappers

import gomoku.server.domain.game.match.OngoingMatch
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

/**
 * Maps a row of the result set to a [OngoingMatch]
 * @see RowMapper
 * @see OngoingMatch
 */
class OngoingMatchRowMapper : RowMapper<OngoingMatch> {

    /**
     * Maps a row of the result set to a [OngoingMatch]
     * @param rs The result set to map
     * @param ctx The statement context
     * @return The [OngoingMatch] from the result set
     */
    override fun map(rs: ResultSet, ctx: StatementContext): OngoingMatch {
        val moveContainer = getMoveContainerFromRS(rs)

        val matchState = rs.getString("match_state").uppercase()
        if (matchState != "ONGOING") {
            throw IllegalArgumentException("Expected a finished match, but found $matchState state")
        }

        return getOngoingMatchFromRS(rs, moveContainer)
    }
}
