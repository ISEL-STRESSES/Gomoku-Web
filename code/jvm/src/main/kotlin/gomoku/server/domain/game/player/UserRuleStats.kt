package gomoku.server.domain.game.player

import gomoku.server.domain.game.rules.Rule

/**
 * Represents the statistics of a rule
 * @property rule rule
 * @property gamesPlayed number of games played with this rule
 * @property elo elo of the rule (0-4000)
 */
data class UserRuleStats(val rule: Rule, val gamesPlayed: Int, val elo: Int) {
    init {
        require(gamesPlayed >= 0)
        require(elo in 0..4000)
    }
}