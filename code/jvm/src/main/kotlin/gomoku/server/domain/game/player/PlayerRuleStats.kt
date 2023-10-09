package gomoku.server.domain.game.player

import gomoku.server.domain.game.rules.Rule

/**
 * Represents the statistics of a rule
 * @property rule rule
 * @property gamesPlayed number of games played with this rule
 * @property elo elo of the rule
 */
data class PlayerRuleStats(val rule: Rule, val gamesPlayed: Int, val elo: Int) {
    init {
        check(gamesPlayed >= 0)
        check(elo >= 0)
    }
}