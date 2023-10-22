package gomoku.server.http.controllers.game.models

import gomoku.server.domain.game.match.MoveContainer

/**
 * Represents the moves of a match in a container
 * to be sent from the API
 * @param boardSize the size of the board
 * @param orderOfMoves the moves of the match
 */
data class MoveContainerOutputModel(
    val boardSize: Int,
    val orderOfMoves: List<MoveOutputModel>,
){
    constructor(moveContainer: MoveContainer): this(
        boardSize = moveContainer.boardSize,
        orderOfMoves = moveContainer.getMoves().map { MoveOutputModel(it) }
    )
}