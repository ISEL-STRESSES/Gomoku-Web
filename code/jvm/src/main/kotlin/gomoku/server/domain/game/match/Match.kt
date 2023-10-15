package gomoku.server.domain.game.match

import gomoku.server.domain.game.Board
import gomoku.server.domain.game.player.Color
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
    val board: Board
) {
    fun getPlayerByColor(color: Color): Player {
        return when (color) {
            playerA.color -> playerA
            playerB.color -> playerB
            else -> throw IllegalArgumentException("There can't be a player matching the color $color")
        }
    }

    fun copy(
        matchId: Int = this.matchId,
        playerA: Player = this.playerA,
        playerB: Player = this.playerB,
        rules: Rules = this.rules,
        board: Board = this.board,
        matchOutcome: MatchOutcome? = null
    ): Match {
        return when (this) {
            is OngoingMatch -> OngoingMatch(matchId, playerA, playerB, rules, board)
            is FinishedMatch -> FinishedMatch(matchId, playerA, playerB, rules, board, matchOutcome!!)
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
    moves: Board
) : Match(matchId, playerA, playerB, rules, moves) {

    val turn = (moves.getMoves().size).toColor()
}

/**
 * Represents a game that has been finished.
 */
class FinishedMatch(
    matchId: Int,
    playerA: Player,
    playerB: Player,
    rules: Rules,
    moves: Board,
    val matchOutcome: MatchOutcome
) : Match(matchId, playerA, playerB, rules, moves) {

    fun getWinnerOrNull(): Player? {
        return matchOutcome.winnerColor?.let { getPlayerByColor(it) }
    }
}
