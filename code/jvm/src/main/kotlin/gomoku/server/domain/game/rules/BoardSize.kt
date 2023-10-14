package gomoku.server.domain.game.rules

import gomoku.server.domain.game.player.Position

/**
 * Represents the possible sizes of a match board
 *
 * @property max The maximum value of the x and y coordinates
 * @throws IllegalArgumentException If the maximum value is negative
 */
enum class BoardSize(private val max: Int) {
    X15(15),
    X19(19);

    init {
        require(max >= 0)
    }

    /**
     * Returns all possible positions on the board
     */
    fun getAllPositions(): List<Position> {
        return (0 until max).flatMap { x ->
            (0 until max).map { y ->
                Position(x, y)
            }
        }
    }
}

/**
 * Deserializes a board size from an integer
 * @receiver The integer to deserialize
 * @return The deserialized board size
 */
fun Int.toBoardSize(): BoardSize {
    return when (this) {
        15 -> BoardSize.X15
        19 -> BoardSize.X19
        else -> throw IllegalArgumentException("Invalid board size: $this")
    }
}
