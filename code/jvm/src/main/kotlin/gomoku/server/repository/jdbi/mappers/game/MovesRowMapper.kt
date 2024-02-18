package gomoku.server.repository.jdbi.mappers.game

import gomoku.server.domain.game.game.move.Move
import gomoku.server.repository.jdbi.mappers.getMoveContainerFromRS
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

/**
 * Maps a row of the result set to a [List] of [Move]s
 * @see RowMapper
 * @see Move
 */
class MovesRowMapper : RowMapper<List<Move>> {
    /**
     * Maps a row of the result set to a [List] of [Move]s
     * @param rs The result set to map
     * @param ctx The statement context
     * @return The [List] of [Move]s from the result set
     */
    override fun map(rs: ResultSet, ctx: StatementContext): List<Move> {
        val moveContainer = getMoveContainerFromRS(rs)
        return moveContainer.getMoves()
    }
}
