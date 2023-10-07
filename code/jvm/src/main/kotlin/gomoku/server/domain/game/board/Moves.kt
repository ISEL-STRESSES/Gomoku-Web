package gomoku.server.domain.game.board

data class Move(
    val position: Position,
    val color: Color
)

data class MatchMoves(
    val black: List<Position>,
    val white: List<Position>
)

