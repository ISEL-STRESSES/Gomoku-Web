package gomoku.server.domain.game.game.move

import gomoku.server.domain.game.game.CellColor
import gomoku.server.domain.game.game.toColor

/**
 * Represents a container for the moves of a game, that doesn't provide data structure information to the outside.
 * Uses a List of [Move] to keep track of the order of moves, and an array of [CellColor] to keep track and do searches on the board.
 * @param boardSize The size of the board.
 * @param orderOfMoves The list of moves in the order they were played.
 * @param board The board as an array of [CellColor].
 */
class MoveContainer private constructor(
    val boardSize: Int,
    private val orderOfMoves: List<Move>,
    private val board: Array<CellColor?>
) {

    private val maxAmountOfMoves = boardSize * boardSize

    val maxIndex = maxAmountOfMoves - 1

    /**
     * Adds a move to the board.
     * This function only ensures that the position is inside the board and that it's not already occupied.
     * Any further verifications if the move is valid or not, must be verified by the rules of the game.
     * @param move The move to be added.
     * @return new [MoveContainer] with the move added or null if there is already a move at the given position.
     */
    fun addMove(move: Move): MoveContainer? {
        val position = move.position
        if (hasMove(position)) return null
        val newBoard = board.copyOf()
        newBoard[position.toIndex(boardSize - 1)] = move.cellColor
        return MoveContainer(boardSize, orderOfMoves + move, newBoard)
    }

    /**
     * Gets the last made move.
     * @return The last made move, or null if no move has been made yet.
     */
    fun getLastMoveOrNull(): Move? = orderOfMoves.lastOrNull()

    /**
     * Checks if a move exists at the given position.
     * @param position The position to check.
     * @return true if a move exists at the position, false otherwise.
     */
    fun hasMove(position: Position): Boolean {
        return board[position.toIndex(boardSize - 1)] != null
    }

    /**
     * Gets all moves from the board by order of play.
     * @return A list of [Move] in the order they were played.
     */
    fun getMoves(): List<Move> {
        return orderOfMoves
    }

    /**
     * Gets the empty positions on the board.
     * @return A list of [Position] that are empty.
     */
    fun getEmptyPositions(): List<Position> {
        return (0..maxIndex)
            .map { it.toPosition(boardSize) }
            .filterNot { hasMove(it) }
    }

    /**
     * Returns true if the board is full, false otherwise.
     */
    fun isFull(): Boolean {
        return orderOfMoves.size == maxAmountOfMoves
    }

    /**
     * Returns true if the board is empty, false otherwise.
     */
    fun isEmpty(): Boolean {
        return orderOfMoves.isEmpty()
    }

    companion object {
        /**
         * Creates an empty board of the given size.
         * @param boardSize The size of the board.
         * @return A new empty [MoveContainer].
         */
        fun createEmptyMoveContainer(boardSize: Int): MoveContainer {
            val moveContainer = Array<CellColor?>(boardSize * boardSize) { null }
            return MoveContainer(boardSize, emptyList(), moveContainer)
        }

        /**
         * Factory method to create a board from a list of moves.
         * @param boardSize The size of the board.
         * @param movesIndexes The list of moves.
         * @return a new [MoveContainer] with the given moves or null if it failed to build the MoveContainer.
         */
        fun buildMoveContainer(
            boardSize: Int,
            movesIndexes: List<Int>
        ): MoveContainer? {
            var moveContainer = createEmptyMoveContainer(boardSize)
            for ((index, boardIndex) in movesIndexes.withIndex()) {
                val position = boardIndex.toPosition(boardSize)
                val color = index.toColor()
                val move = Move(position, color)
                moveContainer = moveContainer.addMove(move) ?: return null
            }
            return moveContainer
        }
    }
}

private fun Position.toIndex(size: Int): Int {
    return this.y * (size + 1) + this.x
}

private fun Int.toPosition(boardSize: Int): Position {
    val x = this % boardSize
    val y = this / boardSize
    return Position(x, y)
}
