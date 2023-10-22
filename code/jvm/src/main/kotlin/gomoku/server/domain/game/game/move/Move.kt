package gomoku.server.domain.game.game.move

import gomoku.server.domain.game.game.Color

/**
 * Represents a move on the board
 * @param position The position of the move
 * @param color The color of the player who made the move
 */
data class Move(
    val position: Position,
    val color: Color
)
