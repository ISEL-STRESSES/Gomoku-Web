package gomoku.server.http.controllers.game.models

/**
 * Represents the rules to be sent from the API
 * @param rulesList the rules
 */
data class GetRulesOutputModel(
    val rulesList: List<RuleOutputModel>
)