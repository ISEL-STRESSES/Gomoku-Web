package gomoku.server.domain.game

import gomoku.server.domain.game.board.Position

enum class BoardSize(private val max: Int) {
    X15(15),
    X19(19);

    fun isPositionInside(position: Position) =
        this.max > position.x && this.max > position.y
}


data class Rules(
    val boardSize: BoardSize,
)

val defaultRules = Rules(
    boardSize = BoardSize.X15,
)
