package gomoku.server.domain.game.player

import gomoku.server.domain.game.rules.BoardSize
import gomoku.server.domain.game.rules.StandardRules
import gomoku.server.domain.user.UserRuleStats
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UserRulesStatsTests {
    @Test
    fun `PlayerRuleStats initializes with valid values`() {
        val stats = UserRuleStats(1, 10, 2000)

        assertEquals(1, stats.ruleId)
        assertEquals(10, stats.gamesPlayed)
        assertEquals(2000, stats.elo)
    }

    @Test
    fun `PlayerRuleStats throws exception for negative gamesPlayed`() {
        assertThrows<IllegalArgumentException> {
            UserRuleStats(1, -1, 2000)
        }
    }

    @Test
    fun `PlayerRuleStats throws exception for elo less than 0`() {
        assertThrows<IllegalArgumentException> {
            UserRuleStats(1, 10, -1)
        }
    }

    @Test
    fun `PlayerRuleStats throws exception for elo greater than 4000`() {
        assertThrows<IllegalArgumentException> {
            UserRuleStats(1, 10, 4001)
        }
    }
}
