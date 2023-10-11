package gomoku.server.domain.game.rules

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class RuleTests {

    @Test
    fun `buildRule constructs StandardRules for STANDARD variant and FREE opening`() {
        val rule = buildRule(15, "STANDARD", "FREE")
        assertTrue(rule is StandardRules)
        assertEquals(BoardSize.X15, rule.boardSize)
        assertEquals(OpeningRule.FREE, rule.openingRule)
    }

    @Test
    fun `buildRule constructs ProOpeningRules for STANDARD variant and PRO opening`() {
        val rule = buildRule(19, "STANDARD", "PRO")
        assertTrue(rule is ProOpeningRules)
        assertEquals(BoardSize.X19, rule.boardSize)
        assertEquals(OpeningRule.PRO, rule.openingRule)
    }

    // Additional test cases for other rule variants if they exist or are added in the future

    @Test
    fun `buildRule throws exception for invalid variant`() {
        assertThrows(IllegalArgumentException::class.java) {
            buildRule(15, "INVALID_VARIANT", "FREE")
        }
    }

    @Test
    fun `buildRule throws exception for invalid openingRule`() {
        assertThrows(IllegalArgumentException::class.java) {
            buildRule(15, "STANDARD", "INVALID_OPENING")
        }
    }

    @Test
    fun `buildRule throws exception for invalid boardMaxSize`() {
        assertThrows(IllegalArgumentException::class.java) {
            buildRule(1111, "STANDARD", "FREE")
        }
    }
}
