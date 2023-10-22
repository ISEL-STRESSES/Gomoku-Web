package gomoku.server.domain.game.game

/**
 * Represents the outcome of a game
 * @property winnerColor the color of the winner or null if the game ended in a draw
 * @property BLACK_WON black won the game
 * @property WHITE_WON white won the game
 * @property DRAW the game ended in a draw
 */
enum class GameOutcome(val winnerColor: Color? = null) {
    BLACK_WON(Color.BLACK),
    WHITE_WON(Color.WHITE),
    DRAW
}

/**
 * Converts a string to a [GameOutcome]
 * @receiver the string to convert
 * @return the [GameOutcome]
 */
fun String.toGameOutcome() = when (this.uppercase()) {
    "BLACK_WON" -> GameOutcome.BLACK_WON
    "WHITE_WON" -> GameOutcome.WHITE_WON
    "DRAW" -> GameOutcome.DRAW
    else -> throw IllegalArgumentException("Invalid game outcome: $this")
}

/**
 * Converts a color to a [GameOutcome]
 * @receiver the color to convert
 * @return the [GameOutcome]
 */
fun Color?.toGameOutcome() = when (this) {
    Color.BLACK -> GameOutcome.BLACK_WON
    Color.WHITE -> GameOutcome.WHITE_WON
    null -> GameOutcome.DRAW
}
