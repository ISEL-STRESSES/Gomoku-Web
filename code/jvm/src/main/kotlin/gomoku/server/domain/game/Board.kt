package gomoku.server.domain.game

import gomoku.server.domain.game.player.Color
import gomoku.server.domain.game.player.Move
import gomoku.server.domain.game.player.Position

/**
 * Represents the board of a game.
 */
class Board private constructor(
    private val size: Int,
    private val listOfMoves: List<Move>,
    private val boardMap: Map<Position, Move>
) {

    companion object {
        fun createEmptyBoard(size: Int): Board {
            return Board(size, emptyList(), emptyMap())
        }
    }

    /**
     * Adds a move to the board.
     */
    fun addMove(move: Move): Board {
        if (!isPositionInside(move.position)) throw IllegalArgumentException("Position is outside the board")
        val newListOfMoves = listOfMoves + move
        val newBoardMap = boardMap + (move.position to move)
        return Board(this.size, newListOfMoves, newBoardMap)
    }

    /**
     * Gets move from the board.
     */
    fun getMove(position: Position): Move? {
        return boardMap[position]
    }

    /**
     * Gets all moves from the board by order of play.
     */
    fun getMoves(): List<Move> {
        return listOfMoves
    }

    /**
     * Gets all moves from the board by color.
     * @param color The color of the moves to get
     * @return The moves
     */
    fun getMovesByColor(color: Color): List<Move> {
        return listOfMoves.filter { it.color == color }
    }

    /**
     * Removes the last move from the board and returns a new Board.
     */
    fun undoLastMove(): Board {
        if (listOfMoves.isEmpty()) return this
        val lastMove = listOfMoves.last()
        val newListOfMoves = listOfMoves.dropLast(1)
        val newBoardMap = boardMap - lastMove.position
        return Board(this.size, newListOfMoves, newBoardMap)
    }

    /**
     * Resets the whole board.
     */
    fun reset(): Board = createEmptyBoard(this.size)

    /**
     * Checks if a position is inside the board
     * @param position The position to check
     * @return true if the position is inside the board, false otherwise
     */
    private fun isPositionInside(position: Position) =
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