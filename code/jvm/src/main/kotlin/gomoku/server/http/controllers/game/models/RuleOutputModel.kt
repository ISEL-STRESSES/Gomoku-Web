package gomoku.server.http.controllers.game.models

import gomoku.server.domain.game.rules.Rules

/**
 * Represents the rule to be sent from the API
 * @param ruleId the id of the rule
 * @param boardSize the board size of the rule
 * @param variant the variant of the rule
 * @param openingRule the opening rule of the rule
 */
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