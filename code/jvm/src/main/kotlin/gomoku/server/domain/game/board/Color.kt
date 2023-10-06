package gomoku.server.domain.game.board

/**
 * Represents a piece or color that can be played on GOMOKU
 */
enum class Color {
    BLACK, WHITE;

    /**
     * Returns the other color depending on the current color
     * @return the other color
     */
    fun other(): Color {
        return if (this == BLACK) WHITE else BLACK
    }
}

/**
 * Helper function to determine if an index is even or odd to decide the color
 * Current structure of the moves list makes it so that pair indexes are black and odd indexes are white
 * @return the color corresponding to the index
 */
fun Int.toColor(): Color {
    val mod = this % 2
    return if (mod == 0) {
        Color.BLACK
    } else {
        Color.WHITE
    }
}
