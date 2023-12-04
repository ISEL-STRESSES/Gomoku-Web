package gomoku.server.http.controllers.user.models

import gomoku.server.domain.user.RankingUserData

/**
 * Represents the statistics of a user for a specific rule
 * to be sent from the API
 * @property gamesPlayed number of games played by the user for this rule
 * @property elo elo of the user for this rule
 */
data class UserRuleStatsOutputModel(
    val id: Int,
    val username: String,
    val gamesPlayed: Int,
    val elo: Int
) {
    constructor(userRuleStats: RankingUserData) : this(
        id = userRuleStats.uuid,
        username = userRuleStats.username,
        gamesPlayed = userRuleStats.gamesPlayed,
        elo = userRuleStats.elo
    )
}
