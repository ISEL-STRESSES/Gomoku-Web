package gomoku.server.domain.game.board

/**
 * Represents the possible sizes of a match board
 * @param max The maximum value of the x and y coordinates
 */
enum class BoardSize(val max: Int) {
    X15(15),
    X19(19);

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

    companion object {

        /**
         * Deserializes a board size from an integer
         * @param int The integer to deserialize from
         * @return The deserialized board size
         */
        fun fromInt(int: Int): BoardSize {
            return when (int) {
                15 -> X15
                19 -> X19
                else -> throw IllegalArgumentException("Invalid board size: $int")
            }
        }
    }
}
