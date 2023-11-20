package gomoku.server.domain.game.game.move

/**
 * Represents a data point
 * @param x The x coordinate
 * @param y The y coordinate
 * @throws IllegalArgumentException If any of the coordinates is negative or greater than max
 */
data class Position(
    val x: Int,
    val y: Int
)
