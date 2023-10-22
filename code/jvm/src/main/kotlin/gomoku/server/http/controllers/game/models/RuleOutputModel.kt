package gomoku.server.http.controllers.game.models

import gomoku.server.domain.game.rules.Rules

data class RuleOutputModel(
    val ruleId: Int,
    val boardSize: Int,
    val variant: String,
    val openingRule: String
){
    constructor(rule: Rules) : this(
        ruleId = rule.ruleId,
        boardSize = rule.boardSize.value,
        variant = rule.variant.name,
        openingRule = rule.openingRule.name
    )
}