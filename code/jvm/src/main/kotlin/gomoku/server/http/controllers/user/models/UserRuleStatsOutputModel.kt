package gomoku.server.http.controllers.user.models

import gomoku.server.domain.user.UserRuleStats

data class UserRuleStatsOutputModel(
    val ruleId: Int,
    val gamesPlayed: Int,
    val elo: Int
){
    constructor(userRuleStats: UserRuleStats) : this(
        ruleId = userRuleStats.ruleId,
        gamesPlayed = userRuleStats.gamesPlayed,
        elo = userRuleStats.elo
    )
}