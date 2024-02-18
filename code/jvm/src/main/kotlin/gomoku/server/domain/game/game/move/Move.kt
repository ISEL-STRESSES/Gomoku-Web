package gomoku.server.domain.game.game.move

import gomoku.server.domain.game.game.CellColor

/**
 * Represents a move on the board
 * @param position The position of the move
 * @param cellColor The color of the player who made the move
 */
data class Move(
    val position: Position,
    val cellColor: CellColor
)
