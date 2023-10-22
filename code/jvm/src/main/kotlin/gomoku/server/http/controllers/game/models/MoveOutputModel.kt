package gomoku.server.http.controllers.game.models

import gomoku.server.domain.game.game.move.Move

/**
 * Represents the move to be sent from the API
 * @param position the position of the move
 * @param color the color of the move
 */
data class MoveOutputModel(
    val position: Int,
    val color: String
) {
    constructor(move: Move) : this(
        position = move.position.value,
        color = move.color.name
    )
}
