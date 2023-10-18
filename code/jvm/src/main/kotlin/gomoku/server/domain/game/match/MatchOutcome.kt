package gomoku.server.domain.game.match

/**
 * Represents the outcome of a match
 * @property winnerColor the color of the winner or null if the match ended in a draw
 * @property BLACK_WON black won the match
 * @property WHITE_WON white won the match
 * @property DRAW the match ended in a draw
 */
enum class MatchOutcome(val winnerColor: Color? = null) {
    BLACK_WON(Color.BLACK),
    WHITE_WON(Color.WHITE),
    DRAW
}

/**
 * Converts a string to a [MatchOutcome]
 * @receiver the string to convert
 * @return the [MatchOutcome]
 */
fun String.toMatchOutcome() = when (this.uppercase()) {
    "BLACK_WON" -> MatchOutcome.BLACK_WON
    "WHITE_WON" -> MatchOutcome.WHITE_WON
    "DRAW" -> MatchOutcome.DRAW
    else -> throw IllegalArgumentException("Invalid match outcome: $this")
}

/**
 * Converts a color to a [MatchOutcome]
 * @receiver the color to convert
 * @return the [MatchOutcome]
 */
fun Color?.toMatchOutcome() = when (this) {
    Color.BLACK -> MatchOutcome.BLACK_WON
    Color.WHITE -> MatchOutcome.WHITE_WON
    null -> MatchOutcome.DRAW
}
