package gomoku.server.http.model

import com.fasterxml.jackson.databind.JsonNode

data class LobbiesResponse(
    val lobbiesList: List<LobbyResponse>
)

data class LobbyResponse(
    val id: Int,
    val rule: LobbyRuleResponse,
    val userId: Int
)

data class LobbyRuleResponse(
    val ruleId: Int,
    val boardSize: String,
    val type: String
)

fun JsonNode.toLobbiesResponse(): LobbiesResponse =
    LobbiesResponse(
        lobbiesList = this["lobbies"].map { it.toLobbyResponse() }
    )

fun JsonNode.toLobbyResponse(): LobbyResponse =
    LobbyResponse(
        id = this["id"].asInt(),
        rule = this["rule"].toLobbyRuleResponse(),
        userId = this["userId"].asInt()
    )

fun JsonNode.toLobbyRuleResponse(): LobbyRuleResponse {
    return LobbyRuleResponse(
        ruleId = this["ruleId"].asInt(),
        boardSize = this["boardSize"].asText(),
        type = this["type"].asText()
    )
}
