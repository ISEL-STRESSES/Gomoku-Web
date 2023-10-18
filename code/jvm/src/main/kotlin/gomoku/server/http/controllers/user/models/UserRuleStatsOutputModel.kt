package gomoku.server.http.controllers.user.models

import gomoku.server.domain.user.UserRuleStats

/**
 * Represents the statistics of a user for a specific rule
 * to be sent from the API
 * @property ruleId unique identifier of the rule
 * @property gamesPlayed number of games played by the user for this rule
 * @property elo elo of the user for this rule
 */
data class UserRuleStatsOutputModel(
    val ruleId: Int,
    val gamesPlayed: Int,
    val elo: Int
) {
    constructor(userRuleStats: UserRuleStats) : this(
        ruleId = userRuleStats.ruleId,
        gamesPlayed = userRuleStats.gamesPlayed,
        elo = userRuleStats.elo
    )
}
