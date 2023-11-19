package gomoku.server.http.model

data class PlayerRuleStatsResponse(
    val id: Int,
    val username: String,
    val ruleId: Int,
    val gamesPlayed: Int,
    val elo: Int,
)