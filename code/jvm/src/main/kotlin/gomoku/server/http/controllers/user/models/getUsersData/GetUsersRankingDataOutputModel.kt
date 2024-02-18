package gomoku.server.http.controllers.user.models.getUsersData

import gomoku.server.http.controllers.user.models.UserRuleStatsOutputModel

/**
 * Represents a list of users with their statistics
 * to be sent from the API
 * @property userData list of users with their statistics
 * @property ruleId the id of the rule of the statistics
 * @property search the username to search
 */
data class GetUsersRankingDataOutputModel(
    val userData: List<UserRuleStatsOutputModel>,
    val ruleId: Int,
    val search: String
)
