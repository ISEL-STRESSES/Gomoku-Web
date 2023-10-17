package gomoku.server.domain.game.match

/**
 * Represents the outcome of a match
 */
enum class MatchOutcome(val winnerColor: Color? = null) {
    BLACK_WON(Color.BLACK),
    WHITE_WON(Color.WHITE),
    DRAW
}

fun String.toMatchOutcome() = when (this) {
    "BLACK_WON" -> MatchOutcome.BLACK_WON
    "WHITE_WON" -> MatchOutcome.WHITE_WON
    "DRAW" -> MatchOutcome.DRAW
    else -> throw IllegalArgumentException("Invalid match outcome: $this")
}

fun Color?.toMatchOutcome() = when (this) {
    Color.BLACK -> MatchOutcome.BLACK_WON
    Color.WHITE -> MatchOutcome.WHITE_WON
    null -> MatchOutcome.DRAW
}
