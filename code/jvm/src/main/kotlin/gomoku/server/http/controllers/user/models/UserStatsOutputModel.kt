package gomoku.server.http.controllers.user.models

import gomoku.server.domain.user.UserRuleStats

data class UserStatsOutputModel(
    val userId: Int,
    val username: String,
    val userRuleStats: List<UserRuleStats>
)
