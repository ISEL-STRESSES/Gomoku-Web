package gomoku.server.domain.game.board

/**
 * Way that Moves are serialized, as a list of positions,
 * this way you can preseve the order of moves, and which colors have been played
 */
typealias SerializedMoves = List<Position>

/***
 *
 */
fun SerializedMoves.nextColorTurn() = (this.size + 1).toColor()


