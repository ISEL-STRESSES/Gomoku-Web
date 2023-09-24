package gomoku.server.domain.game.board

/**
 * Represents a data point
 *
 * TODO:
 */
data class Position(
    val x: Int,
    val y: Int,
) {
    init {
        check(x >= 0)
        check(y >= 0)
    }
}
