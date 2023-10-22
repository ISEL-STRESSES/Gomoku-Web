package gomoku.server.domain.game.match

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Represents a Mixin for [MoveContainer] to be used by Jackson.
 * @property boardSize The size of the board
 * @property orderOfMoves The order of the moves
 * @property board The board
 */
abstract class MoveContainerMixin private constructor(
    @JsonProperty("boardSize") val boardSize: Int,
    @JsonProperty("orderOfMoves") private val orderOfMoves: List<Move> = emptyList(),
    @JsonProperty("board") private val board: Array<Color?> = Array(boardSize * boardSize) { null }
)
