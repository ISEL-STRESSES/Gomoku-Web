package gomoku.server.domain.game.board

/**
 * Way that Moves are serialized, as a list of positions,
 * this way you can preserve the order of moves, and which colors have been played
 */
typealias SerializedMoves = List<Position>

fun SerializedMoves.nextMoveColor() = (this.size + 1).toColor()

fun SerializedMoves.getMoves(color: Color) = when (color) {
    Color.BLACK -> this.getBlackMoves()
    Color.WHITE -> this.getWhiteMoves()
}

fun SerializedMoves.getBlackMoves() = this.filterIndexed { index, _ -> index % 2 == 0 }
fun SerializedMoves.getWhiteMoves() = this.filterIndexed { index, _ -> index % 2 == 1 }
