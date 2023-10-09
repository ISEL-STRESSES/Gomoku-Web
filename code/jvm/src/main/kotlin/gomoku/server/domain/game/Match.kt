package gomoku.server.domain.game

import gomoku.server.domain.game.board.Position
import gomoku.server.domain.game.player.Move
import gomoku.server.domain.game.player.toColor
import gomoku.server.domain.game.rules.Rule
import gomoku.utils.failure

/**
 * Represents a game that can be played.
 */
sealed class Match(
    val gameID: Int,
    val playerA: Int,
    val playerB: Int,
    val rules: Rule,
    val isPlayerABlack: Boolean,
    val moves: List<Move> = emptyList()
)

/**
 * Represents a game that is currently being played.
 */
class OngoingMatch(
    gameID: Int,
    playerA: Int,
    playerB: Int,
    rules: Rule,
    isPlayerABlack: Boolean,
    moves: List<Move>
) : Match(gameID, playerA, playerB, rules, isPlayerABlack, moves) {

    val turn = (moves.size).toColor()

    private fun possiblePositions(moves: List<Move>): List<Position> =
        rules.possibleMoves(moves, (moves.size).toColor()).map { it.position }

    private fun isValidPosition(position: Position): Boolean =
        rules.isValidMove(moves, Move(position, turn))

    fun makeMove(move: Move): Match { // TODO: Return MakeMoveResult (Either<MakeMoveError, Game>)
        if (!isValidPosition(move.position)) {
            failure(IllegalMoveException())
        }

        if (turn != move.color) {
            failure(NotYourTurnException())
        }
        TODO("Continue, check if move is winning move, etc.")
    }

    fun isWinningMove(moves: List<Move>, currentMove: Move): Boolean = rules.isWinningMove(moves, currentMove)

    fun isDraw(): Boolean = possiblePositions(moves).isEmpty()
}

/**
 * Represents a game that has been finished.
 */
class FinishedGame(
    gameID: Int,
    playerA: Int,
    playerB: Int,
    rules: Rule,
    isPlayerABlack: Boolean,
    moves: List<Move>,
    val matchOutcome: MatchOutcome
) : Match(gameID, playerA, playerB, rules, isPlayerABlack, moves) {

    fun getWinner(): Int? {
        // Implement logic to get the winner.
        TODO()
    }
}
