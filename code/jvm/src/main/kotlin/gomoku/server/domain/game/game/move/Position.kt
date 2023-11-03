package gomoku.server.domain.game.game.move

/**
 * Represents a data point
 * @param x The x coordinate
 * @param y The y coordinate
 * @param max The maximum value for x and y
 * @throws IllegalArgumentException If any of the coordinates is negative or greater than max
 */
data class Position(
    val x: Int,
    val y: Int,
    val max: Int
) {
    init {
        require(x in 0..max && y in 0..max) {
            "Coordinates must be between 0 and $max"
        }
    }
}
