package gomoku.server.http.controllers.game.models

import gomoku.server.domain.game.match.Move

data class MoveOutputModel(
    val position: Int,
    val color: String
){
    constructor(move: Move) : this(
        position = move.position.value,
        color = move.color.name
    )
}
