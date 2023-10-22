package gomoku.server.domain.game.game.move

/**
 * Represents a data point
 * @param value The value of the coordinate
 * @throws IllegalArgumentException If any of the coordinates is negative
 */
data class Position(
    val value: Int
) {
    init {
        require(value >= 0) { "Position cannot be negative" }
    }
}
