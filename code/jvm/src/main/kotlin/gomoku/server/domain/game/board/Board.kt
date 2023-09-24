package gomoku.server.domain.game.board

import gomoku.server.domain.game.InvalidBoardException

data class Board(
    val grid: Map<Position, Color>
)

/**
 * Helper function to retrieve a color at a certain position.
 * Use this instead of accessing the internals of the board.
 * @return the color at the given position, or null if there is no color at that position.
 */
fun Board.at(position: Position) =
    grid[position]


fun SerializedMoves.toBoard(): Result<Board> {
    val finalGrid = this.foldIndexed(mapOf<Position, Color>()) { index, grid, position ->
        val existing = grid[position]
        if (existing != null) return Result.failure(InvalidBoardException())

        val color = index.toColor()

        grid + (position to color)
    }

    return Result.success(
        Board(
            grid = finalGrid,
        )
    )
}
