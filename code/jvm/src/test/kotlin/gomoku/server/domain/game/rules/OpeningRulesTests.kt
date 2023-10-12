package gomoku.server.domain.game.rules

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class OpeningRulesTests {

    @Test
    fun `toOpeningRule correctly deserializes valid strings`() {
        assertEquals(OpeningRule.FREE, "FREE".toOpeningRule())
        assertEquals(OpeningRule.PRO, "PRO".toOpeningRule())
    }

    @Test
    fun `toOpeningRule throws exception for invalid strings`() {
        assertThrows<IllegalArgumentException> {
            "INVALID".toOpeningRule() // An invalid opening rule string
        }
    }
}
