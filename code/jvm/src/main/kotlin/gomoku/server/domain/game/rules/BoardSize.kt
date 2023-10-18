package gomoku.server.domain.game.rules

/**
 * Represents the possible sizes of a match board
 * @property value The maximum value of the x and y coordinates
 * @property X15 A 15x15 board
 * @property X19 A 19x19 board
 * @throws IllegalArgumentException If the maximum value is negative
 */
enum class BoardSize(val value: Int) {
    X15(15),
    X19(19);

    init {
        require(value >= 0)
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
