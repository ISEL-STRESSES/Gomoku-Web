package gomoku.server.domain.game.rules

/**
 * Represents the statistics of a rule
 * @property rule rule
 * @property gamesPlayed number of games played with this rule
 * @property elo elo of the rule
 */
class RuleStats(val rule: Rule, val gamesPlayed: Int, val elo: Int)
