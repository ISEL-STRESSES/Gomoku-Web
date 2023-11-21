package gomoku.server.domain.game.game

import gomoku.server.domain.game.game.move.MoveContainer
import gomoku.server.domain.game.rules.Rules

/**
 * Represents a game.
 * @property id The id of the game
 * @property playerBlack The id of the player playing with black stones
 * @property playerWhite The id of the player playing with white stones
 * @property rules The rules of the game
 * @property moveContainer The container of the moves
 */
sealed class Game(
    val id: Int,
    val playerBlack: Int,
    val playerWhite: Int,
    val rules: Rules,
    val moveContainer: MoveContainer
)

/**
 * Represents a game that is currently being played.
 * @property turn The color of the player that has to play
 */
class OngoingGame(
    id: Int,
    playerBlack: Int,
    playerWhite: Int,
    rules: Rules,
    moves: MoveContainer
) : Game(id, playerBlack, playerWhite, rules, moves) {

    val turn = (moves.getMoves().size).toColor()
}

/**
 * Represents a game that has been finished.
 * @property gameOutcome The outcome of the game
 */
class FinishedGame(
    id: Int,
    playerBlack: Int,
    playerWhite: Int,
    rules: Rules,
    moves: MoveContainer,
    val gameOutcome: GameOutcome
) : Game(id, playerBlack, playerWhite, rules, moves) {

    /**
     * Gets the winner id or null if the game ended in a draw.
     * @return the winner id or null
     */
    fun getWinnerIdOrNull(): Int? {
        return gameOutcome.let {
            when (it) {
                GameOutcome.BLACK_WON -> playerBlack
                GameOutcome.WHITE_WON -> playerWhite
                GameOutcome.DRAW -> null
            }
        }
    }
}
