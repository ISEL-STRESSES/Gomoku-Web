package gomoku.server.http.controllers.game.models

import gomoku.server.domain.game.game.move.Move
import gomoku.server.domain.game.game.move.Position

/**
 * Represents the move to be sent from the API
 * @param position the position of the move
 * @param color the color of the move
 */
data class MoveOutputModel(
    val position: Position,
    val color: String
) {
    constructor(move: Move) : this(
        position = move.position,
        color = move.cellColor.name
    )
}
