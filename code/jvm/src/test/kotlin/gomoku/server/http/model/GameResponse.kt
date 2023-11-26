package gomoku.server.http.model

import com.fasterxml.jackson.databind.JsonNode

data class GameResponse(
    val id: Int,
    val playerBlack: Int,
    val playerWhite: Int,
    val rule: RuleResponse,
    val moves: MovesResponse,
    val gameOutcome: String?,
    val turn: Int?,
    val type: String
)

data class MovesResponse(
    val boardSize: Int,
    val orderOfMoves: List<String>
)

fun JsonNode.toGameResponse(): GameResponse =
    GameResponse(
        id = this["id"].asInt(),
        playerBlack = this["playerBlack"].asInt(),
        playerWhite = this["playerWhite"].asInt(),
        rule = this["rule"].toRuleResponse(),
        moves = this["moves"].toMovesResponse(),
        gameOutcome = this["gameOutcome"].asTextOrNull(),
        turn = this["turn"].asIntOrNull(),
        type = this["type"].asText()
    )

fun JsonNode.toMovesResponse(): MovesResponse =
    MovesResponse(
        boardSize = this["boardSize"].asInt(),
        orderOfMoves = this["orderOfMoves"].map { it.asText() }
    )

fun JsonNode.asTextOrNull(): String? {
    return if (this.asText() == "null") null else this.asText()
}

fun JsonNode.asIntOrNull(): Int? {
    return if (this.asText() == "null") null else this.asInt()
}
