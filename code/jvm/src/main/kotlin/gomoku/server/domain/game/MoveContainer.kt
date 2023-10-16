package gomoku.server.domain.game

import gomoku.server.domain.game.player.Color
import gomoku.server.domain.game.player.Move
import gomoku.server.domain.game.player.Position
import gomoku.utils.Either
import gomoku.utils.failure
import gomoku.utils.success

typealias AddMoveResult = Either<AddMoveError, MoveContainer>

sealed class AddMoveError {
    object ImpossiblePosition : AddMoveError()
    object AlreadyOccupied : AddMoveError()
}

/**
 * Represents the board of a game.
 */
class MoveContainer private constructor(
    private val size: Int,
    private val listOfMoves: List<Move>,
    private val board: Array<Array<Color?>>
) {

    companion object {
        /**
         * Creates an empty board of the given size.
         * @param size The size of the board
         * @return The empty board
         */
        fun createEmptyMoveContainer(size: Int): MoveContainer {
            val board = Array(size) { Array<Color?>(size) { null } }
            return MoveContainer(size, emptyList(), board)
        }

        /**
         * Factory method to create a board from a list of moves.
         * @param size The size of the board.
         * @param movesList The list of moves.
         * @return A board instance with the specified moves.
         */
        fun createBoardWithMoves(size: Int, movesList: List<Move>): AddMoveResult {
            val board = createEmptyMoveContainer(size)

            for (move in movesList){
                val addMoveResult = board.addMove(move)
                if (addMoveResult is Either.Left){
                    return failure(addMoveResult.value)
                }
            }

            return success(board)
        }
    }

    /**
     * Adds a move to the board.
     */
    fun addMove(move: Move) : AddMoveResult {
        val position = move.position


        return if (!isPositionInside(position)) {
            failure(AddMoveError.ImpossiblePosition)
        } else if (board[position.x][position.y] == null) {
            board[position.x][position.y] = move.color
            success(MoveContainer(size, listOfMoves + move, board))
        } else {
            failure(AddMoveError.AlreadyOccupied)
        }


    }

    /**
     * Helper function to check if move is already done
     */
    fun hasMove(position: Position): Boolean = board[position.x][position.y] != null

    /**
     * Gets all moves from the board by order of play.
     */
    fun getMoves(): List<Move> {
        return listOfMoves
    }

    /**
     * Resets the whole board.
     */
    fun reset(): MoveContainer = createEmptyMoveContainer(this.size)

    /**
     * Checks if a position is inside the board
     * @param position The position to check
     * @return true if the position is inside the board, false otherwise
     */
    fun isPositionInside(position: Position) =
        position.x in 0 until size && position.y in 0 until size

    /**
     * Returns true if the board is full, false otherwise
     */
    fun isFull(): Boolean {
        return listOfMoves.size == size * size
    }

    /**
     * Returns true if the board is empty, false otherwise
     */
    fun isEmpty(): Boolean {
        return listOfMoves.isEmpty()
    }
}
