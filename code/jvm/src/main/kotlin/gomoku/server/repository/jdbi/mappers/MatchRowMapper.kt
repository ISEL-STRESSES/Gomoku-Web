package gomoku.server.repository.jdbi.mappers

import gomoku.server.domain.game.match.Match
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

class MatchRowMapper : RowMapper<Match> {

    // TODO: implement
    override fun map(rs: ResultSet, ctx: StatementContext): Match {
        return when (rs.getString("match_state")) {
//            "ongoing" -> OngoingMatch(
//                matchId = rs.getInt("id"),
//                playerA = ,
//                playerB = ,
//                rules = ,
//                moves = ,
//            )
//            "finished" ->FinishedMatch(
//                matchId = rs.getInt("id"),
//                playerA = ,
//                playerB = ,
//                rules = ,
//                moves = ,
//                matchOutcome = ,
//            )
            else -> throw IllegalArgumentException("Invalid match state")
        }
    }
}
