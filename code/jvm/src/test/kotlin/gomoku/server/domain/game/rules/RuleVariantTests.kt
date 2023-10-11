package gomoku.server.domain.game.rules

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class RuleVariantTests {

    @Test
    fun `toRuleVariant correctly deserializes valid strings`() {
        assertEquals(RuleVariant.STANDARD, "STANDARD".toRuleVariant())
    }

    @Test
    fun `toRuleVariant throws exception for invalid strings`() {
        assertThrows(IllegalArgumentException::class.java) {
            "INVALID".toRuleVariant() // An invalid rule variant string
        }
    }
}
