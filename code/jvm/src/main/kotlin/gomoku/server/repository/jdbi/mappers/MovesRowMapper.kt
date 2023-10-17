package gomoku.server.repository.jdbi.mappers

import gomoku.server.domain.game.match.AddMoveError
import gomoku.server.domain.game.match.Move
import gomoku.server.domain.game.match.MoveContainer.Companion.buildMoveContainer
import gomoku.utils.Failure
import gomoku.utils.Success
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

class MovesRowMapper : RowMapper<List<Move>> {
    override fun map(rs: ResultSet, ctx: StatementContext): List<Move> {
        val boardSize = rs.getInt("board_size")
        val movesIndexes = (rs.getArray("moves").array as Array<*>).map { it as Int }

        val addMoveResult = buildMoveContainer(boardSize, movesIndexes)

        return when (addMoveResult) {
            is Success -> addMoveResult.value.getMoves()
            is Failure -> when (addMoveResult.value) {
                AddMoveError.ImpossiblePosition -> throw IllegalArgumentException("Invalid position")
                AddMoveError.AlreadyOccupied -> throw IllegalArgumentException("Position already occupied") // TODO: THROW APPROPRIATE EXCEPTION
            }
        }
    }
}
