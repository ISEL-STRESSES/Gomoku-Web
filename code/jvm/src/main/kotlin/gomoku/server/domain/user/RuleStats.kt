package gomoku.server.domain.user

/**
 * Represents the stats of a rule
 * @property ruleId the id of the rule
 * @property gamesPlayed the number of games played with this rule
 * @property elo the elo of the rule
 */
data class RuleStats(
    val ruleId: Int,
    val rank: Int,
    val gamesPlayed: Int,
    val elo: Int
)
