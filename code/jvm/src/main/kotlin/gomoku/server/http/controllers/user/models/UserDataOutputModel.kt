package gomoku.server.http.controllers.user.models

import gomoku.server.domain.user.ListUserData
import gomoku.server.domain.user.UserData

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
