package gomoku.server.domain.game.game

/**
 * Represents the outcome of a game
 * @property winnerColor the color of the winner or null if the game ended in a draw
 * @property BLACK_WON black won the game
 * @property WHITE_WON white won the game
 * @property DRAW the game ended in a draw
 */
enum class GameOutcome(val winnerColor: CellColor? = null) {
    BLACK_WON(CellColor.BLACK),
    WHITE_WON(CellColor.WHITE),
    DRAW
}

/**
 * Converts a color to a [GameOutcome]
 * @receiver the color to convert
 * @return the [GameOutcome]
 */
fun CellColor?.toGameOutcome() = when (this) {
    CellColor.BLACK -> GameOutcome.BLACK_WON
    CellColor.WHITE -> GameOutcome.WHITE_WON
    null -> GameOutcome.DRAW
}
