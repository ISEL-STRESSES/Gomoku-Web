package gomoku.server.domain.game.board

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
        check(x >= 0)
        check(y >= 0)
    }
}
