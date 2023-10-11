package gomoku.server.domain.game.player

/**
 * Represents a data point
 *
 * @param x The x coordinate
 * @param y The y coordinate
 * @throws IllegalArgumentException If any of the coordinates is negative
 */
data class Position(
    val x: Int,
    val y: Int
) {
    init {
        require(x >= 0)
        require(y >= 0)
    }
}
