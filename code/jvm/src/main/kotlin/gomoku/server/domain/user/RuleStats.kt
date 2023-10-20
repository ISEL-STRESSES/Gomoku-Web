package gomoku.server.domain.user

data class RuleStats(
    val ruleId: Int,
    val gamesPlayed: Int,
    val elo: Int
)