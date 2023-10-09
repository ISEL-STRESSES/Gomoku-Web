package gomoku.server.domain.game

import gomoku.server.domain.game.player.Color

/**
 * Represents the outcome of a match
 */
enum class MatchOutcome {
    BLACK_WON,
    WHITE_WON,
    DRAW;

    /**
     * Returns the winner color, or null in case of a draw
     */
    fun getWinnerColorOrNull(): Color? {
        return when (this) {
            BLACK_WON -> Color.BLACK
            WHITE_WON -> Color.WHITE
            DRAW -> null
        }
    }
}
