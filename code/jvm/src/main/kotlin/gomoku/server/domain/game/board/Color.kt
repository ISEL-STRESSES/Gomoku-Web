package gomoku.server.domain.game.board

enum class Color {
    BLACK, WHITE;

    fun other(): Color {
        return if (this == BLACK) WHITE else BLACK
    }
}

fun Int.toColor(): Color {
    val mod = this % 2
    return if (mod == 0) {
        Color.BLACK
    } else {
        Color.WHITE
    }
}
