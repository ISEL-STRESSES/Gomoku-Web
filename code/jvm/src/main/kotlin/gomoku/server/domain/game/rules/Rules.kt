package gomoku.server.domain.game.rules

import gomoku.server.domain.game.board.BoardSize
import gomoku.server.domain.game.board.Position
import gomoku.server.domain.game.player.Color
import gomoku.server.domain.game.player.Move

/**
 * Represents a rule
 * @property boardSize size of the board
 * @property variant variant of the rule
 * @property openingRule opening rule
 */
sealed class Rule {

    abstract val boardSize: BoardSize
    abstract val variant: RuleVariant
    abstract val openingRule: OpeningRule

    /**
     * Checks if a move is valid based on the rules of the match
     * @param move previous moves of the match
     * @param move move to check
     * @return true if the move is valid, false otherwise
     */
    abstract fun isValidMove(previousMoves: List<Move>, move: Move): Boolean

    /**
     * Returns the possible moves based on the rules of the match
     * @param previousMoves previous moves of the match
     * @param color color of the player
     * @return the possible moves
     */
    abstract fun possibleMoves(previousMoves: List<Move>, color: Color): List<Move>

    /**
     * Checks if a move is a winning move
     * @param moves previous moves of the match
     * @param currentMove move to check
     * @return true if the move is a winning move, false otherwise
     */
    abstract fun isWinningMove(moves: List<Move>, currentMove: Move): Boolean

    companion object {

        /**
         * Builds a rule based on the given parameters
         * @param boardMaxSize size of the board
         * @param variantName variant of the rule
         * @param openingRuleName opening rule
         * @return the class of the rule
         */
        fun buildRule(boardMaxSize: Int, variantName: String, openingRuleName: String): Rule {
            val variant = RuleVariant.fromString(variantName)
            val openingRule = OpeningRule.fromString(openingRuleName)
            val boardSize = BoardSize.fromInt(boardMaxSize)

            when (variant) {
                RuleVariant.STANDARD -> {
                    return when (openingRule) {
                        OpeningRule.FREE -> {
                            StandardRule(boardSize)
                        }
                        OpeningRule.PRO -> {
                            ProOpeningRule(boardSize)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Represents a Standard rule set
 * @constructor creates a default rule with board size 15, standard variant and free opening rule
 */
data class StandardRule(override val boardSize: BoardSize) : Rule() {

    override val variant: RuleVariant = RuleVariant.STANDARD
    override val openingRule: OpeningRule = OpeningRule.FREE

    override fun isValidMove(previousMoves: List<Move>, move: Move): Boolean {
        val occupiedPositions = previousMoves.map { it.position }
        val currentColor = move.color
        val currentMove = move.position
        val allPositions = (0 until boardSize.ordinal).flatMap { x ->
            (0 until boardSize.ordinal).map { y ->
                Position(x, y)
            }
        }
        return allPositions.filterNot { it in occupiedPositions }.contains(currentMove) && currentColor != previousMoves.last().color
    }

    override fun possibleMoves(previousMoves: List<Move>, color: Color): List<Move> {
        val occupiedPositions = previousMoves.map { it.position }
        val allPositions = (0 until boardSize.ordinal).flatMap { x ->
            (0 until boardSize.ordinal).map { y ->
                Position(x, y)
            }
        }
        return allPositions.filterNot { it in occupiedPositions }.map { Move(it, color) }
    }

    override fun isWinningMove(moves: List<Move>, currentMove: Move): Boolean {
        val directions = listOf(
            Pair(0, 1), // Horizontal
            Pair(1, 0), // Vertical
            Pair(1, 1), // Diagonal (top-left to bottom-right)
            Pair(1, -1) // Diagonal (top-right to bottom-left)
        )

        for (direction in directions) {
            if (countPieces(moves, currentMove.position, currentMove.color, direction.first, direction.second) +
                countPieces(moves, currentMove.position, currentMove.color, -direction.first, -direction.second) - 1 >= 5
            ) {
                return true
            }
        }

        return false
    }

    private fun countPieces(moves: List<Move>, position: Position, color: Color, dx: Int, dy: Int): Int {
        var count = 0
        var x = position.x + dx
        var y = position.y + dy
        while (moves.contains(Move(Position(x, y), color))) {
            count++
            x += dx
            y += dy
        }
        return count
    }
}

data class ProOpeningRule(override val boardSize: BoardSize) : Rule() {
    override val variant: RuleVariant = RuleVariant.STANDARD
    override val openingRule: OpeningRule = OpeningRule.PRO
    override fun isValidMove(previousMoves: List<Move>, move: Move): Boolean {
        TODO("Not yet implemented")
    }

    override fun possibleMoves(previousMoves: List<Move>, color: Color): List<Move> {
        TODO("Not yet implemented")
    }

    override fun isWinningMove(moves: List<Move>, currentMove: Move): Boolean {
        TODO("Not yet implemented")
    }
}
