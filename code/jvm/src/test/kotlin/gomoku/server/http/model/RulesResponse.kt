package gomoku.server.http.model

import com.fasterxml.jackson.databind.JsonNode

data class RulesResponse(
    val rulesList: List<RuleResponse>
)

data class RuleResponse(
    val ruleId: Int,
    val boardSize: Int,
    val variant: String,
    val openingRule: String
)

fun JsonNode.toRulesResponse(): RulesResponse =
    RulesResponse(
        rulesList = this["rulesList"].map { it.toRuleResponse() }
    )

fun JsonNode.toRuleResponse(): RuleResponse {
    return RuleResponse(
        ruleId = this["ruleId"].asInt(),
        boardSize = this["boardSize"].asInt(),
        variant = this["variant"].asText(),
        openingRule = this["openingRule"].asText()
    )
}
