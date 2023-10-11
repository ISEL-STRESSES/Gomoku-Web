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
