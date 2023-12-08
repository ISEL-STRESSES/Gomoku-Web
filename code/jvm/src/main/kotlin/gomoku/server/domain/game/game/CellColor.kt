package gomoku.server.domain.game.game

/**
 * Represents a piece or color that can be played
 * @property BLACK black color
 * @property WHITE white color
 */
enum class CellColor {
    BLACK,
    WHITE;

    /**
     * Returns the other color depending on the current color
     * @return the other color
     */
    fun other(): CellColor {
        return if (this == BLACK) WHITE else BLACK
    }
}

data class Turn(val color: CellColor, val user: Int)

/**
 * Helper function to convert an index to a color, depending on it's an odd or even number
 * Current structure of the move list makes it so that pair indexes are black and odd indexes are white
 * @receiver the index to convert
 * @return the color corresponding to the index
 */
fun Int.toColor(): CellColor {
    val mod = this % 2
    return if (mod == 0) {
        CellColor.BLACK
    } else {
        CellColor.WHITE
    }
}

fun CellColor.toTurn(black: Int, white: Int) = Turn(this, if (this == CellColor.BLACK) black else white)
