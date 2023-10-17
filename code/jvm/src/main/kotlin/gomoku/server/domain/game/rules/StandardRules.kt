package gomoku.server.domain.game.rules

import gomoku.server.domain.game.match.Color
import gomoku.server.domain.game.match.Move
import gomoku.server.domain.game.match.MoveContainer
import gomoku.server.domain.game.match.Position
import gomoku.utils.failure
import gomoku.utils.success

/**
 * Represents a Standard rule set
 * @constructor creates a default rule with board size 15, standard variant and free opening rule
 */
data class StandardRules(override val ruleId: Int, override val boardSize: BoardSize) : Rules() {
    override val variant: RuleVariant = RuleVariant.STANDARD
    override val openingRule: OpeningRule = OpeningRule.FREE

    override fun isValidMove(moveContainer: MoveContainer, move: Move, turn: Color): IsValidMoveResult {
        if (turn != move.color) return failure(MoveError.InvalidTurn)
        if (!moveContainer.isPositionInside(move.position)) return failure(MoveError.ImpossiblePosition)
        if (moveContainer.hasMove(move.position)) return failure(MoveError.AlreadyOccupied)

        return success(Unit)
    }

    override fun possibleMoves(moveContainer: MoveContainer, color: Color): List<Move> {
        return (0..moveContainer.maxIndex)
            .map { Position(it) }
            .filterNot { moveContainer.hasMove(it) }
            .map { Move(it, color) }
    }

    override fun isWinningMove(moveContainer: MoveContainer, move: Move): Boolean {
        val directions = listOf(
            Pair(0, 1), // Horizontal
            Pair(1, 0), // Vertical
            Pair(1, 1), // Diagonal (top-left to bottom-right)
            Pair(1, -1) // Diagonal (top-right to bottom-left)
        )

        for (direction in directions) {
            if (countPieces(moveContainer, move.position, move.color, direction.first, direction.second) +
                countPieces(moveContainer, move.position, move.color, -direction.first, -direction.second) + 1 >= 5
            ) {
                return true
            }
        }

        return false
    }

    private fun countPieces(
        moveContainer: MoveContainer,
        position: Position,
        color: Color,
        dx: Int,
        dy: Int
    ): Int {
        var count = 0
        var x = position.value % moveContainer.boardSize + dx
        var y = position.value / moveContainer.boardSize + dy

        while (moveContainer.hasMove(Position(y * moveContainer.boardSize + x)) &&
            moveContainer.getMoves().any { it.position == Position(y * moveContainer.boardSize + x) && it.color == color }
        ) {
            count++
            x += dx
            y += dy
        }
        return count
    }
}
