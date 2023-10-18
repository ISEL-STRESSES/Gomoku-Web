package gomoku.server.repository.jdbi.mappers

import gomoku.server.domain.game.match.AddMoveError
import gomoku.server.domain.game.match.MoveContainer
import gomoku.server.domain.game.match.MoveContainer.Companion.buildMoveContainer
import gomoku.utils.Failure
import gomoku.utils.Success
import java.sql.ResultSet

fun getMoveContainer(rs: ResultSet): MoveContainer {
    val boardSize = rs.getInt("board_size")
    val moves = (rs.getArray("moves").array as Array<*>).map { it as Int }
    val moveContainerResult = buildMoveContainer(boardSize, moves)
    return when (moveContainerResult) {
        is Success -> moveContainerResult.value
        is Failure -> when (moveContainerResult.value) {
            AddMoveError.ImpossiblePosition -> throw IllegalArgumentException("Invalid position")
            AddMoveError.AlreadyOccupied -> throw IllegalArgumentException("Position already occupied")
        }
    }
}
