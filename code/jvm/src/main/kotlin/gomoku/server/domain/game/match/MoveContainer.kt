package gomoku.server.domain.game.match

import gomoku.utils.Either
import gomoku.utils.Failure
import gomoku.utils.Success
import gomoku.utils.failure
import gomoku.utils.success

typealias AddMoveResult = Either<AddMoveError, MoveContainer>

sealed class AddMoveError {
    object ImpossiblePosition : AddMoveError()
    object AlreadyOccupied : AddMoveError()
}

/**
 * Represents a container for the moves of a game, that doesn't provide data structure information to the outside.
 * Uses a List of [Move] to keep track of the order of moves, and an array of [Color] to keep track and do searches on the board.
 * @param boardSize The size of the board.
 * @param orderOfMoves The list of moves in the order they were played.
 * @param board The board as an array of [Color].
 */
class MoveContainer private constructor(
    val boardSize: Int,
    private val orderOfMoves: List<Move>,
    private val board: Array<Color?>
) {

    private val maxAmountOfMoves = boardSize * boardSize
    val maxIndex = maxAmountOfMoves - 1

    /**
     * Adds a move to the board.
     * This function only ensures that the position is inside the board and that it's not already occupied.
     * Any further verifications if the move is valid or not, must be verified by the rules of the game.
     * @param move The move to be added.
     * @return [AddMoveResult] which is either an error or a new [MoveContainer] with the move added.
     */
    fun addMove(move: Move): AddMoveResult {
        val position = move.position

        return if (!isPositionInside(position)) {
            failure(AddMoveError.ImpossiblePosition)
        } else if (hasMove(position)) {
            failure(AddMoveError.AlreadyOccupied)
        } else {
            val newBoard = board.copyOf()
            newBoard[position.value] = move.color
            success(MoveContainer(boardSize, orderOfMoves + move, newBoard))
        }
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
        return board[position.value] != null
    }

    /**
     * Gets all moves from the board by order of play.
     * @return A list of [Move] in the order they were played.
     */
    fun getMoves(): List<Move> {
        return orderOfMoves
    }

    /**
     * Checks if a given position is inside the board's bounds.
     * @param position The position to check.
     * @return true if the position is inside the board, false otherwise.
     */
    fun isPositionInside(position: Position): Boolean {
        return position.value in 0..maxIndex
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
            val moveContainer = Array<Color?>(boardSize * boardSize) { null }
            return MoveContainer(boardSize, emptyList(), moveContainer)
        }

        /**
         * Factory method to create a board from a list of moves.
         * @param boardSize The size of the board.
         * @param movesIndexes The list of moves.
         * @return [AddMoveResult] which is either an error or a new [MoveContainer] with the given moves.
         */
        fun buildMoveContainer(boardSize: Int, movesIndexes: List<Int>): AddMoveResult {
            var moveContainer = createEmptyMoveContainer(boardSize)
            for ((index, boardIndex) in movesIndexes.withIndex()) {
                val position = Position(boardIndex)
                val color = index.toColor()
                val move = Move(position, color)
                val addMoveResult = moveContainer.addMove(move)
                when (addMoveResult) {
                    is Success -> moveContainer = addMoveResult.value
                    is Failure -> return failure(addMoveResult.value)
                }
            }
            return success(moveContainer)
        }
    }
}
