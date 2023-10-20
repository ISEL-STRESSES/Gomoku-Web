package gomoku.server.http.controllers.user.models

import gomoku.server.domain.user.RuleStats

/**
 * Represents the output model for a rule statistics
 * to be sent from the API
 * @property ruleId The id of the rule
 * @property gamesPlayed The number of games played
 * @property elo The elo of the rule
 */
data class RuleStatsOutputModel(
    val ruleId: Int,
    val gamesPlayed: Int,
    val elo: Int
) {
    constructor(ruleStats: RuleStats) : this(
        ruleId = ruleStats.ruleId,
        gamesPlayed = ruleStats.gamesPlayed,
        elo = ruleStats.elo
    )
}
