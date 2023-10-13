package gomoku.server.domain.game.match

import gomoku.server.domain.game.player.Color
import gomoku.server.domain.game.player.Move
import gomoku.server.domain.game.player.Player
import gomoku.server.domain.game.player.toColor
import gomoku.server.domain.game.rules.Rules

/**
 * Represents a game that can be played.
 */
sealed class Match(
    val matchId: Int,
    val playerA: Player,
    val playerB: Player,
    val rules: Rules,
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
    matchId: Int,
    playerA: Player,
    playerB: Player,
    rules: Rules,
    moves: List<Move>
) : Match(matchId, playerA, playerB, rules, moves) {

    val turn = (moves.size).toColor()
}

/**
 * Represents a game that has been finished.
 */
class FinishedGame(
    matchId: Int,
    playerA: Player,
    playerB: Player,
    rules: Rules,
    moves: List<Move>,
    private val matchOutcome: MatchOutcome
) : Match(matchId, playerA, playerB, rules, moves) {

    fun getWinnerOrNull(): Player? {
        return matchOutcome.winnerColor?.let { getPlayerByColor(it) }
    }
}
