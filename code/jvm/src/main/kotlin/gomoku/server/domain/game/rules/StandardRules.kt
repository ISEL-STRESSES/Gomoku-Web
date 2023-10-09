package gomoku.server.domain.game.rules

import gomoku.server.domain.game.board.BoardSize
import gomoku.server.domain.game.player.Color
import gomoku.server.domain.game.player.Move

/**
 * Represents a Standard rule set
 * @constructor creates a default rule with board size 15, standard variant and free opening rule
 */
data class StandardRules(override val boardSize: BoardSize) : Rule() {

    override val variant: RuleVariant = RuleVariant.STANDARD
    override val openingRule: OpeningRule = OpeningRule.FREE

    override fun isValidMove(previousMoves: List<Move>, move: Move): Boolean {
        val occupiedPositions = previousMoves.map { it.position }
        val currentColor = move.color
        val currentMove = move.position
        val allMoves = (0 until boardSize.ordinal).flatMap { x ->
            (0 until boardSize.ordinal).map { y ->
                gomoku.server.domain.game.board.Position(x, y)
            }
        }
        return allMoves.filterNot { it in occupiedPositions }
            .contains(currentMove) && currentColor != previousMoves.last().color
    }

    override fun possibleMoves(previousMoves: List<Move>, color: Color): List<Move> {
        val occupiedPositions = previousMoves.map { it.position }
        val allMoves = (0 until boardSize.ordinal).flatMap { x ->
            (0 until boardSize.ordinal).map { y ->
                gomoku.server.domain.game.board.Position(x, y)
            }
        }
        return allMoves.filterNot { it in occupiedPositions }.map { Move(it, color) }
    }

    override fun isWinningMove(previousMoves: List<Move>, move: Move): Boolean {
        val directions = listOf(
            Pair(0, 1), // Horizontal
            Pair(1, 0), // Vertical
            Pair(1, 1), // Diagonal (top-left to bottom-right)
            Pair(1, -1) // Diagonal (top-right to bottom-left)
        )

        for (direction in directions) {
            if (countPieces(previousMoves, move.position, move.color, direction.first, direction.second) +
                countPieces(previousMoves, move.position, move.color, -direction.first, -direction.second) - 1 >= 5
            ) {
                return true
            }
        }

        return false
    }

    private fun countPieces(
        moves: List<Move>,
        position: gomoku.server.domain.game.board.Position,
        color: Color,
        dx: Int,
        dy: Int
    ): Int {
        var count = 0
        var x = position.x + dx
        var y = position.y + dy
        while (moves.contains(Move(gomoku.server.domain.game.board.Position(x, y), color))) {
            count++
            x += dx
            y += dy
        }
        return count
    }
}