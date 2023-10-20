package gomoku.server.domain.game.match

import com.fasterxml.jackson.annotation.JsonProperty

abstract class MoveContainerMixin private constructor(
    @JsonProperty("boardSize") val boardSize: Int,
    @JsonProperty("orderOfMoves") private val orderOfMoves: List<Move> = emptyList(),
    @JsonProperty("board") private val board: Array<Color?> = Array(boardSize * boardSize) { null }
)
