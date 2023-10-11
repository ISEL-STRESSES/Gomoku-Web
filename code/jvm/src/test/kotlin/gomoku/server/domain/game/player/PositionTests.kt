package gomoku.server.domain.game.player

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PositionTests {

    @Test
    fun `Position initializes with valid values`() {
        val position = Position(10, 12)

        assertEquals(10, position.x)
        assertEquals(12, position.y)
    }

    @Test
    fun `Position throws exception for negative x`() {
        assertThrows<IllegalArgumentException> {
            Position(-1, 12)
        }
    }

    @Test
    fun `Position throws exception for negative y`() {
        assertThrows<IllegalArgumentException> {
            Position(10, -1)
        }
    }
}
