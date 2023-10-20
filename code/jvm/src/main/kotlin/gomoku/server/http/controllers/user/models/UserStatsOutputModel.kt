package gomoku.server.http.controllers.user.models

import gomoku.server.domain.user.UserStats

/**
 * Represents the User with its statistics
 * to be sent from the API
 * @property userId unique identifier of the user
 * @property username username of the user
 * @property userRuleStats list of statistics of the user for each rule
 */
data class UserStatsOutputModel(
    val userId: Int,
    val username: String,
    val userRuleStats: List<RuleStatsOutputModel>
) {
    constructor(userStats: UserStats) : this(
        userId = userStats.uuid,
        username = userStats.username,
        userRuleStats = userStats.userRuleStats.map { RuleStatsOutputModel(it.ruleId, it.gamesPlayed, it.elo) }
    )
}
