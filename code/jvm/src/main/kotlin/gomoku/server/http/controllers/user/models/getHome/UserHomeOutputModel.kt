package gomoku.server.http.controllers.user.models.getHome

import gomoku.server.domain.user.RuleStats

/**
 * Represents a user output model
 * to be sent from the API
 * @property username username of the user
 */
data class UserHomeOutputModel(
    val userId: Int,
    val username: String,
    val userStats: List<RuleStats>
)
