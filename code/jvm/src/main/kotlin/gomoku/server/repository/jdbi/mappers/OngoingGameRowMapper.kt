package gomoku.server.repository.jdbi.mappers

import gomoku.server.domain.game.game.OngoingGame
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

/**
 * Maps a row of the result set to a [OngoingGame]
 * @see RowMapper
 * @see OngoingGame
 */
class OngoingGameRowMapper : RowMapper<OngoingGame> {

    /**
     * Maps a row of the result set to a [OngoingGame]
     * @param rs The result set to map
     * @param ctx The statement context
     * @return The [OngoingGame] from the result set
     */
    override fun map(rs: ResultSet, ctx: StatementContext): OngoingGame {
        val moveContainer = getMoveContainerFromRS(rs)

        val gameState = rs.getString("match_state").uppercase()
        if (gameState != "ONGOING") {
            throw IllegalArgumentException("Expected a finished game, but found $gameState state")
        }

        return getOngoingGameFromRS(rs, moveContainer)
    }
}
