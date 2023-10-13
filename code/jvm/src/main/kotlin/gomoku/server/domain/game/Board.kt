package gomoku.server.domain.game

import gomoku.server.domain.game.player.Color
import gomoku.server.domain.game.player.Move
import gomoku.server.domain.game.player.Position

/**
 * Represents the board of a game.
 */
class Board {
    // List of moves made in the game with index to represent the order of the moves
    private val listOfMoves = mutableListOf<Move>()

    // Map of moves made in the game with the position of the move as key
    private val boardMap = mutableMapOf<Position, Move>()

    /**
     * Adds a move to the board.
     */
    fun addMove(move: Move) {
        listOfMoves.add(move)
        boardMap[move.position] = move
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
     */
    fun getMovesByColor(color: Color): List<Move> {
        return listOfMoves.filter { it.color == color }
    }
}
