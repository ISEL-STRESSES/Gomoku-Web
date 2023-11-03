package gomoku.server.repository.jdbi.mappers.game

import gomoku.server.domain.game.game.Game
import gomoku.server.repository.jdbi.mappers.getFinishedGameFromRS
import gomoku.server.repository.jdbi.mappers.getMoveContainerFromRS
import gomoku.server.repository.jdbi.mappers.getOngoingGameFromRS
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

/**
 * Maps a row of the result set to a [Game]
 * @see RowMapper
 * @see Game
 */
class GameRowMapper : RowMapper<Game> {

    /**
     * Maps a row of the result set to a [Game]
     * @param rs The result set to map
     * @param ctx The statement context
     * @return The [Game] from the result set
     */
    override fun map(rs: ResultSet, ctx: StatementContext): Game {
        val moveContainer = getMoveContainerFromRS(rs)

        return when (rs.getString("match_state").uppercase()) {
            "ONGOING" -> getOngoingGameFromRS(rs, moveContainer)
            "FINISHED" -> getFinishedGameFromRS(rs, moveContainer)
            else -> throw IllegalArgumentException("Invalid game state")
        }
    }
}
