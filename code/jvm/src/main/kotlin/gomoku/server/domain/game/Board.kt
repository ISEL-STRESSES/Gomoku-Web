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
     *
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
}