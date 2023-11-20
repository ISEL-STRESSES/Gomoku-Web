package gomoku.server.domain.game.rules

import com.fasterxml.jackson.annotation.JsonIgnore
import gomoku.server.domain.game.IsValidMoveResult
import gomoku.server.domain.game.errors.MoveError
import gomoku.server.domain.game.game.CellColor
import gomoku.server.domain.game.game.move.Move
import gomoku.server.domain.game.game.move.MoveContainer
import gomoku.server.domain.game.game.move.Position
import gomoku.utils.failure
import gomoku.utils.success

/**
 * Represents a Standard rule set
 * @constructor creates a default rule with board size 15, standard variant and free opening rule
 */
data class StandardRules(override val ruleId: Int, override val boardSize: BoardSize) : Rules() {
    @JsonIgnore
    override val variant: RuleVariant = RuleVariant.STANDARD

    @JsonIgnore
    override val openingRule: OpeningRule = OpeningRule.FREE

    val type = "StandardRules"

    /**
     * Checks if a move is valid based on the rules of the game
     * @param moveContainer previous moves of the game
     * @param move move to check
     * @param turn color of the player trying to play
     * @return the move result
     */
    @JsonIgnore
    override fun isValidMove(moveContainer: MoveContainer, move: Move, turn: CellColor): IsValidMoveResult {
        if (turn != move.cellColor) return failure(MoveError.InvalidTurn)
        if (moveContainer.hasMove(move.position)) return failure(MoveError.AlreadyOccupied)

        return success(Unit)
    }

    /**
     * Returns the possible moves based on the rules of the game
     * @param moveContainer previous moves of the game
     * @param cellColor color of the player
     * @param turn color of the player trying to play
     * @return the possible moves possible in the set of rules
     */
    @JsonIgnore
    override fun possiblePositions(
        moveContainer: MoveContainer,
        cellColor: CellColor,
        turn: CellColor
    ): List<Position> {
        return moveContainer.getEmptyPositions()
    }

    /**
     * Checks if a move is a winning move
     * @param moveContainer previous moves of the game
     * @param move move to check if it was a winning move
     * @return true if the move is a winning move, false otherwise
     */
    @JsonIgnore
    override fun isWinningMove(moveContainer: MoveContainer, move: Move): Boolean {
        val directions = listOf(
            Pair(0, 1), // Horizontal
            Pair(1, 0), // Vertical
            Pair(1, 1), // Diagonal (top-left to bottom-right)
            Pair(1, -1) // Diagonal (top-right to bottom-left)
        )

        for (direction in directions) {
            if (countPieces(moveContainer, move.position, move.cellColor, direction.first, direction.second) +
                countPieces(moveContainer, move.position, move.cellColor, -direction.first, -direction.second) + 1 >= 5
            ) {
                return true
            }
        }

        return false
    }

    /**
     * Private function that counts the number of pieces of the same color in a given direction
     * @param moveContainer The move container
     * @param position The position to start counting from
     * @param cellColor The color of the pieces to count
     * @param dx The x direction to count in
     * @param dy The y direction to count in
     * @return The number of pieces of the same color in the given direction
     */
    private fun countPieces(
        moveContainer: MoveContainer,
        position: Position,
        cellColor: CellColor,
        dx: Int,
        dy: Int
    ): Int {
        val size = moveContainer.boardSize - 1
        var count = 0
        var x = position.x + dx
        var y = position.y + dy

        while (x in 0..size && y in 0..size &&
            moveContainer.hasMove(Position(x, y)) &&
            moveContainer.getMoves()
                .any { it.position == Position(x, y) && it.cellColor == cellColor }
        ) {
            count++
            x += dx
            y += dy
        }
        return count
    }
}
