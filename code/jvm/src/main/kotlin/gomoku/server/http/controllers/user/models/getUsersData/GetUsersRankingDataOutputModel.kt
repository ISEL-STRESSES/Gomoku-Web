package gomoku.server.http.controllers.user.models.getUsersData

import gomoku.server.http.controllers.user.models.UserRuleStatsOutputModel

/**
 * Represents a list of users with their statistics
 * to be sent from the API
 * @property userData list of users with their statistics
 */
data class GetUsersRankingDataOutputModel(
    val userData: List<UserRuleStatsOutputModel>,
    val ruleId: Int,
    val search: String
)
