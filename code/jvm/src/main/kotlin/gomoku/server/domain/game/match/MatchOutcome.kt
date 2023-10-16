package gomoku.server.domain.game.match

import gomoku.server.domain.game.player.Color

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