package gomoku.server.http.model

import com.fasterxml.jackson.databind.JsonNode

data class FinishedGamesResponse(
    val gameList: List<GameResponse>
)

fun JsonNode.toFinishedGamesResponse(): FinishedGamesResponse =
    FinishedGamesResponse(
        gameList = this["finishedGames"].map { it.toGameResponse() }
    )
