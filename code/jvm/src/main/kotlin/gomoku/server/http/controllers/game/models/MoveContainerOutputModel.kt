package gomoku.server.http.controllers.game.models

import gomoku.server.domain.game.match.MoveContainer

data class MoveContainerOutputModel(
    val boardSize: Int,
    val orderOfMoves: List<MoveOutputModel>,
){
    constructor(moveContainer: MoveContainer): this(
        boardSize = moveContainer.boardSize,
        orderOfMoves = moveContainer.getMoves().map { MoveOutputModel(it) }
    )
}