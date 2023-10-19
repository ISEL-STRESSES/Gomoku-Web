package gomoku.server.repository.jdbi.mappers

import gomoku.server.domain.game.match.Match
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

/**
 * Maps a row of the result set to a [Match]
 * @see RowMapper
 * @see Match
 */
class MatchRowMapper : RowMapper<Match> {

    /**
     * Maps a row of the result set to a [Match]
     * @param rs The result set to map
     * @param ctx The statement context
     * @return The [Match] from the result set
     */
    override fun map(rs: ResultSet, ctx: StatementContext): Match {
        val moveContainer = getMoveContainerFromRS(rs)

        return when (rs.getString("match_state").uppercase()) {
            "ONGOING" -> getOngoingMatchFromRS(rs, moveContainer)
            "FINISHED" -> getFinishedMatchFromRS(rs, moveContainer)
            else -> throw IllegalArgumentException("Invalid match state")
        }
    }
}
