package gomoku.server.domain.game.match

import gomoku.server.domain.game.player.Color
import gomoku.server.domain.game.player.Move
import gomoku.server.domain.game.player.Player
import gomoku.server.domain.game.player.toColor
import gomoku.server.domain.game.rules.Rule

/**
 * Represents a game that can be played.
 */
sealed class Match(
    val gameID: Int,
    val playerA: Player,
    val playerB: Player,
    val rules: Rule,
    val moves: List<Move> = emptyList()
) {
    fun getPlayerByColor(color: Color): Player {
        return when (color) {
            playerA.color -> playerA
            playerB.color -> playerB
            else -> throw IllegalArgumentException("There can't be a player matching the color $color")
        }
    }
}

/**
 * Represents a game that is currently being played.
 */
class OngoingMatch(
    gameID: Int,
    playerA: Player,
    playerB: Player,
    rules: Rule,
    moves: List<Move>
) : Match(gameID, playerA, playerB, rules, moves) {

    val turn = (moves.size).toColor()
}

/**
 * Represents a game that has been finished.
 */
class FinishedGame(
    gameID: Int,
    playerA: Player,
    playerB: Player,
    rules: Rule,
    moves: List<Move>,
    val matchOutcome: MatchOutcome
) : Match(gameID, playerA, playerB, rules, moves) {

    fun getWinnerOrNull(): Player? {
        return when (matchOutcome) {
            MatchOutcome.BLACK_WON -> getPlayerByColor(Color.BLACK)
            MatchOutcome.WHITE_WON -> getPlayerByColor(Color.WHITE)
            MatchOutcome.DRAW -> null
        }
    }
}
