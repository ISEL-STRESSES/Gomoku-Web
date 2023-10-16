package gomoku.server.domain.game.match

import gomoku.server.domain.game.player.toColor
import gomoku.server.domain.game.rules.Rules

/**
 * Represents a game.
 */
sealed class Match(
    val matchId: Int,
    val playerBlack: Int,
    val playerWhite: Int,
    val rules: Rules,
    val moveContainer: MoveContainer
)

/**
 * Represents a game that is currently being played.
 */
class OngoingMatch(
    matchId: Int,
    playerBlack: Int,
    playerWhite: Int,
    rules: Rules,
    moves: MoveContainer
) : Match(matchId, playerBlack, playerWhite, rules, moves) {

    val turn = (moves.getMoves().size).toColor()
}

/**
 * Represents a game that has been finished.
 */
class FinishedMatch(
    matchId: Int,
    playerBlack: Int,
    playerWhite: Int,
    rules: Rules,
    moves: MoveContainer,
    private val matchOutcome: MatchOutcome
) : Match(matchId, playerBlack, playerWhite, rules, moves) {

    fun getWinnerIdOrNull(): Int? {
        return matchOutcome.let {
            when(it) {
                MatchOutcome.BLACK_WON -> playerBlack
                MatchOutcome.WHITE_WON -> playerWhite
                MatchOutcome.DRAW -> null
            }
        }
    }
}
