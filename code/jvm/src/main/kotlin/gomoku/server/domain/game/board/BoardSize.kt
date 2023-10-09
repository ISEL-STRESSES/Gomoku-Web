package gomoku.server.domain.game.board

/**
 * Represents the possible sizes of a match board
 *
 * @param max The maximum value of the x and y coordinates
 * @throws IllegalArgumentException If the maximum value is negative
 */
enum class BoardSize(val max: Int) {
    X15(15),
    X19(19);

    init {
        check(max >= 0)
    }

    /**
     * Checks if a position is inside the board
     * @param position The position to check
     */
    fun isPositionInside(position: Position) =
        position.x in 0 until max && position.y in 0 until max

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