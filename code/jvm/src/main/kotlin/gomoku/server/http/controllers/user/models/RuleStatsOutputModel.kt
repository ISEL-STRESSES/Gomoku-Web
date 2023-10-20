package gomoku.server.http.controllers.user.models

import gomoku.server.domain.user.RuleStats

data class RuleStatsOutputModel (
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