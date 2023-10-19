package gomoku.server.domain.game.rules

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class RulesVariantTests {

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
