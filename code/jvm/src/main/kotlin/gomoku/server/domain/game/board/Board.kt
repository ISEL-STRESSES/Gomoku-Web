package gomoku.server.domain.game.board

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
