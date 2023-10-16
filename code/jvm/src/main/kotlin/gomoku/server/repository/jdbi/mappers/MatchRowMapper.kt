package gomoku.server.repository.jdbi.mappers

import gomoku.server.domain.game.match.FinishedMatch
import gomoku.server.domain.game.match.Match
import gomoku.server.domain.game.match.MoveContainer.Companion.buildMoveContainer
import gomoku.server.domain.game.match.OngoingMatch
import gomoku.server.domain.game.match.toMatchOutcome
import gomoku.server.domain.game.rules.buildRule
import gomoku.utils.Either
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

class MatchRowMapper : RowMapper<Match> {
    override fun map(rs: ResultSet, ctx: StatementContext): Match {
        val moves = (rs.getArray("moves").array as Array<*>).map { it as Int }
        val moveContainerResult = buildMoveContainer(rs.getInt("board_size"), moves)
        val moveContainer = if (moveContainerResult is Either.Right) moveContainerResult.value
        else throw IllegalArgumentException("Invalid moves array")

        return when (rs.getString("match_state")) {
            "ongoing" -> OngoingMatch(
                matchId = rs.getInt("id"),
                playerBlack = rs.getInt("player_black"),
                playerWhite = rs.getInt("player_white"),
                rules = buildRule(
                    boardMaxSize = rs.getInt("board_size"),
                    variantName = rs.getString("variant"),
                    openingRuleName = rs.getString("opening_rule"),
                ),
                moves = moveContainer,
            )
            "finished" -> FinishedMatch(
                matchId = rs.getInt("id"),
                playerBlack = rs.getInt("player_black"),
                playerWhite = rs.getInt("player_white"),
                rules = buildRule(
                    boardMaxSize = rs.getInt("board_size"),
                    variantName = rs.getString("variant"),
                    openingRuleName = rs.getString("opening_rule"),
                ),
                moves = moveContainer,
                matchOutcome = rs.getString("outcome").uppercase().toMatchOutcome(),
            )
            else -> throw IllegalArgumentException("Invalid match state")
        }
    }
}
