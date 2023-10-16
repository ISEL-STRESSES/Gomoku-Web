package gomoku.server.domain.game.rules

import gomoku.server.domain.game.match.Move
import gomoku.server.domain.game.match.MoveContainer
import gomoku.server.domain.game.match.Position
import gomoku.server.domain.game.player.Color
import gomoku.utils.failure
import gomoku.utils.success

/**
 * Represents a Standard rule set
 * @constructor creates a default rule with board size 15, standard variant and free opening rule
 */
data class StandardRules(override val boardSize: BoardSize) : Rules() {

    override val variant: RuleVariant = RuleVariant.STANDARD
    override val openingRule: OpeningRule = OpeningRule.FREE

    override fun isValidMove(moveContainer: List<Move>, move: Move): IsValidMoveResult {
        val occupiedPositions = moveContainer.map { it.position }
        val currentColor = moveContainer.size.toColor()
        if (currentColor != move.color) return failure(MoveError.InvalidTurn)
        val currentMove = move.position
        val allPositions = boardSize.getAllPositions()
        if (currentMove !in allPositions) return failure(MoveError.ImpossiblePosition)
        if (currentMove in occupiedPositions) return failure(MoveError.AlreadyOccupied)
        return success(Unit)
    }

    override fun possibleMoves(moveContainer: MoveContainer, color: Color): List<Move> {
        val occupiedPositions = moveContainer.getMoves().map { it.position }
        val allPositions = boardSize.getAllPositions()
        return allPositions.filterNot { it in occupiedPositions }.map { Move(it, color) }
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
        var x = position.x + dx
        var y = position.y + dy
        while (moveContainer.hasMove((Position(x, y))) {
            count++
            x += dx
            y += dy
        }
        return count
    }
}
