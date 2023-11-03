package gomoku.server.repository.jdbi.mappers.game

import gomoku.server.domain.game.game.FinishedGame
import gomoku.server.repository.jdbi.mappers.getFinishedGameFromRS
import gomoku.server.repository.jdbi.mappers.getMoveContainerFromRS
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

/**
 * Maps a row of the result set to a [FinishedGame]
 * @see RowMapper
 * @see FinishedGame
 */
class FinishedGameRowMapper : RowMapper<FinishedGame> {

    /**
     * Maps a row of the result set to a [FinishedGame]
     * @param rs The result set to map
     * @param ctx The statement context
     * @return The [FinishedGame] from the result set
     */
    override fun map(rs: ResultSet, ctx: StatementContext): FinishedGame {
        val moveContainer = getMoveContainerFromRS(rs)

        val gameState = rs.getString("match_state").uppercase()
        if (gameState != "FINISHED") {
            throw IllegalArgumentException("Expected a finished game, but found $gameState state")
        }

        return getFinishedGameFromRS(rs, moveContainer)
    }
}
