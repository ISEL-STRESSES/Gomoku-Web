package gomoku.server.domain.game.rules

import gomoku.server.domain.game.IsValidMoveResult
import gomoku.server.domain.game.errors.MoveError
import gomoku.server.domain.game.game.CellColor
import gomoku.server.domain.game.game.Turn
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
    override val variant: RuleVariant = RuleVariant.STANDARD

    override val openingRule: OpeningRule = OpeningRule.PRO

    /**
     * Checks if a move is valid, according to the Pro rule set.
     * @param moveContainer The move container
     * @param move The move to check
     * @param turn The color of the player who made the move
     * @return the move result.
     */
    override fun isValidMove(moveContainer: MoveContainer, move: Move, turn: Turn): IsValidMoveResult {
        val center = Position(boardSize.maxIndex / 2, boardSize.maxIndex / 2)

        when (moveContainer.getMoves().size) {
            0 -> {
                if (turn.color != CellColor.BLACK || turn.color != move.cellColor || move.position != center) {
                    return failure(MoveError.InvalidPosition)
                }
            }

            1 -> { // White's first move anywhere besides the center
                if (turn.color != CellColor.WHITE || turn.color != move.cellColor) {
                    return failure(MoveError.InvalidTurn)
                }
            }

            2 -> {
                if (turn.color != CellColor.BLACK || turn.color != move.cellColor) return failure(MoveError.InvalidTurn)
                val blackFirstMove = moveContainer.getMoves()[0].position
                if (!isTwoSquaresAway(blackFirstMove, move.position)) {
                    return failure(MoveError.InvalidPosition)
                }
            }

            else -> {
                if (turn.color != move.cellColor) return failure(MoveError.InvalidTurn)
            }
        }
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
        val dx = abs(pos1.x - pos2.x)
        val dy = abs(pos1.y - pos2.y)
        return (dx >= 2 && dy == 0) || (dx == 0 && dy >= 2) || (dx >= 2 && dy >= 2)
    }

    /**
     * Returns the possible moves according to the Pro rule set.
     * @param moveContainer The move container
     * @param cellColor The color of the player
     * @return a list with the possible moves.
     */
    override fun possiblePositions(
        moveContainer: MoveContainer,
        cellColor: CellColor,
        turn: CellColor
    ): List<Position> {
        return moveContainer
            .getEmptyPositions()
            .filter { position ->
                when (moveContainer.getMoves().size) {
                    0 -> position == Position(boardSize.maxIndex / 2, boardSize.maxIndex / 2)
                    1 -> true
                    2 -> {
                        val blackFirstMove = moveContainer.getMoves()[0].position
                        isTwoSquaresAway(blackFirstMove, position)
                    }

                    else -> true
                }
            }
    }

    /**
     * Checks if a move is a winning move, according to the Pro rule set.
     * @param moveContainer The move container
     * @param move The move to check
     * @return true if the move is a winning move, false otherwise
     */
    override fun isWinningMove(moveContainer: MoveContainer, move: Move): Boolean {
        return StandardRules(0, boardSize).isWinningMove(moveContainer, move)
    }
}
