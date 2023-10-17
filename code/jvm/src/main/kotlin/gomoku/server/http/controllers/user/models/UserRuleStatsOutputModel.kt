package gomoku.server.http.controllers.user.models

data class UserRuleStatsOutputModel(
    val ruleId: Int,
    val gamesPlayer: Int,
    val elo: Int
)
