package gomoku.server.http.controllers.user.models

import gomoku.server.domain.user.ListUserData
import gomoku.server.domain.user.UserData

/**
 * Represents a user with its statistics listed by rules
 * to be sent from the API
 * @property uuid unique identifier of the user
 * @property username username of the user
 * @property userRuleStats list of user statistics by rules
 */
data class UserDataOutputModel(
    val uuid: Int,
    val username: String,
    val userRuleStats: List<UserRuleStatsOutputModel>
) {
    constructor(userData: UserData) : this(
        uuid = userData.uuid,
        username = userData.username,
        userRuleStats = userData.userRuleStats.map { UserRuleStatsOutputModel(it.ruleId, it.gamesPlayed, it.elo) }
    )

    constructor(userData: ListUserData) : this(
        uuid = userData.uuid,
        username = userData.username,
        userRuleStats = userData.userRuleStats.map { UserRuleStatsOutputModel(it.ruleId, it.gamesPlayed, it.elo) }
    )
}
