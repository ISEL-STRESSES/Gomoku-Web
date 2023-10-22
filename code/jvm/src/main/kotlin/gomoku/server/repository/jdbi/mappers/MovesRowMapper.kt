package gomoku.server.repository.jdbi.mappers

import gomoku.server.domain.game.exceptions.ImpossiblePositionException
import gomoku.server.domain.game.exceptions.PositionAlreadyOccupiedException
import gomoku.server.domain.game.match.AddMoveError
import gomoku.server.domain.game.match.Move
import gomoku.server.domain.game.match.MoveContainer.Companion.buildMoveContainer
import gomoku.utils.Failure
import gomoku.utils.Success
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
        val boardSize = rs.getInt("board_size")
        val movesIndexes = (rs.getArray("moves")?.array as? Array<*>)

        if (movesIndexes == null || movesIndexes.isEmpty()) {
            return emptyList()
        }

        val movesIndexesInt = movesIndexes.map { it as Int }

        val moveContainerResult = buildMoveContainer(boardSize, movesIndexesInt)

        return when (moveContainerResult) {
            is Success -> moveContainerResult.value.getMoves()
            is Failure -> when (moveContainerResult.value) {
                AddMoveError.ImpossiblePosition -> throw ImpossiblePositionException("Failed to Deserialize Moves")
                AddMoveError.AlreadyOccupied -> throw PositionAlreadyOccupiedException("Failed to Deserialize Moves")
            }
        }
    }
}
