package gomoku.server.domain.game.match

import gomoku.server.domain.game.rules.Rules

/**
 * Represents a game.
 * @property matchId The id of the match
 * @property playerBlack The id of the player playing with black stones
 * @property playerWhite The id of the player playing with white stones
 * @property rules The rules of the game
 * @property moveContainer The container of the moves
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
 * @property turn The color of the player that has to play
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
 * @property matchOutcome The outcome of the match
 */
class FinishedMatch(
    matchId: Int,
    playerBlack: Int,
    playerWhite: Int,
    rules: Rules,
    moves: MoveContainer,
    private val matchOutcome: MatchOutcome
) : Match(matchId, playerBlack, playerWhite, rules, moves) {

    /**
     * Gets the winner id or null if the match ended in a draw.
     * @return the winner id or null
     */
    fun getWinnerIdOrNull(): Int? {
        return matchOutcome.let {
            when (it) {
                MatchOutcome.BLACK_WON -> playerBlack
                MatchOutcome.WHITE_WON -> playerWhite
                MatchOutcome.DRAW -> null
            }
        }
    }
}
