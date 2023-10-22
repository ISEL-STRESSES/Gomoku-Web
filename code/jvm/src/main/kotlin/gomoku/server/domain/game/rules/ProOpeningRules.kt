package gomoku.server.domain.game.rules

import com.fasterxml.jackson.annotation.JsonIgnore
import gomoku.server.domain.game.IsValidMoveResult
import gomoku.server.domain.game.errors.MoveError
import gomoku.server.domain.game.game.Color
import gomoku.server.domain.game.game.move.Move
import gomoku.server.domain.game.game.move.MoveContainer
import gomoku.server.domain.game.game.move.Position
import gomoku.utils.failure
import gomoku.utils.success
import kotlin.math.abs

/**
 * Represents the Pro rule set variation.
 */
data class ProOpeningRules(override val ruleId: Int, override val boardSize: BoardSize) : Rules() {
    @JsonIgnore
    override val variant: RuleVariant = RuleVariant.STANDARD

    @JsonIgnore
    override val openingRule: OpeningRule = OpeningRule.PRO

    val type = "ProOpeningRules"

    /**
     * Checks if a move is valid according to the Pro rule set.
     * @param moveContainer The move container
     * @param move The move to check
     * @param turn The color of the player who made the move
     * @return the move result.
     */
    @JsonIgnore
    override fun isValidMove(moveContainer: MoveContainer, move: Move, turn: Color): IsValidMoveResult {
        val center = Position((boardSize.value * boardSize.value) / 2)

        when (moveContainer.getMoves().size) {
            0 -> {
                if (turn != Color.BLACK || move.position != center) {
                    return failure(MoveError.InvalidMove)
                }
            }
            1 -> { // White's first move anywhere besides center
                if (turn != move.color) {
                    return failure(MoveError.InvalidTurn)
                }
            }
            2 -> {
                if (turn != move.color) return failure(MoveError.InvalidTurn)
                val blackFirstMove = moveContainer.getMoves()[0].position
                if (!isTwoSquaresAway(blackFirstMove, move.position)) {
                    return failure(MoveError.InvalidMove)
                }
            }
            else -> {
                if (turn != move.color) return failure(MoveError.InvalidTurn)
            }
        }

        if (!moveContainer.isPositionInside(move.position)) return failure(MoveError.ImpossiblePosition)
        if (moveContainer.hasMove(move.position)) return failure(MoveError.AlreadyOccupied)

        return success(Unit)
    }

    /**
     * Checks if two positions are two squares away from each other.
     * @param pos1 The first position
     * @param pos2 The second position
     * @return true if the positions are two squares away from each other, false otherwise
     */
    private fun isTwoSquaresAway(pos1: Position, pos2: Position): Boolean {
        val dx = abs(pos1.value % boardSize.value - pos2.value % boardSize.value)
        val dy = abs(pos1.value / boardSize.value - pos2.value / boardSize.value)
        return (dx >= 2 && dy == 0) || (dx == 0 && dy >= 2) || (dx >= 2 && dy >= 2)
    }

    /**
     * Returns the possible moves according to the Pro rule set.
     * @param moveContainer The move container
     * @param color The color of the player
     * @return a list with the possible moves.
     */
    @JsonIgnore
    override fun possibleMoves(moveContainer: MoveContainer, color: Color): List<Move> {
        // Logic similar to standard, just filtered based on the Pro opening rules
        return (0..moveContainer.maxIndex)
            .map { Position(it) }
            .filterNot { moveContainer.hasMove(it) }
            .map { Move(it, color) }
    }

    /**
     * Checks if a move is a winning move according to the Pro rule set.
     * @param moveContainer The move container
     * @param move The move to check
     * @return true if the move is a winning move, false otherwise
     */
    @JsonIgnore
    override fun isWinningMove(moveContainer: MoveContainer, move: Move): Boolean {
        return StandardRules(0, boardSize).isWinningMove(moveContainer, move)
    }
}
