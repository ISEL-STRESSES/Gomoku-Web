package gomoku.server.repository.jdbi.mappers

import gomoku.server.domain.game.match.FinishedMatch
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

class FinishedMatchRowMapper : RowMapper<FinishedMatch> {
    override fun map(rs: ResultSet, ctx: StatementContext): FinishedMatch {
        val moveContainer = getMoveContainerFromRS(rs)

        val matchState = rs.getString("match_state").uppercase()
        if (matchState != "FINISHED") {
            throw IllegalArgumentException("Expected a finished match, but found $matchState state")
        }

        return getFinishedMatchFromRS(rs, moveContainer)
    }
}
