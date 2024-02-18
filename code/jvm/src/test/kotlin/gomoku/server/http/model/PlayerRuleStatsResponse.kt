package gomoku.server.http.model

data class PlayerRuleStatsResponse(
    val id: Int,
    val username: String,
    val gamesPlayed: Int,
    val elo: Int
)
