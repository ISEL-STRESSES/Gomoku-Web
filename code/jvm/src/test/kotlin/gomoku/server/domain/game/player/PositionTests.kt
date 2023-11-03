package gomoku.server.domain.game.player

import gomoku.server.domain.game.game.move.Position
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PositionTests {

    @Test
    fun `Position initializes with valid values`() {
        val position = Position(10, 10, 11)

        assertEquals(10, position.x)
        assertEquals(10, position.y)
        assertEquals(11, position.max)
    }

    @Test
    fun `Position does not initialize for invalid values`() {
        assertThrows<IllegalArgumentException> {
            Position(-1, 0, 0)
        }

        assertThrows<IllegalArgumentException> {
            Position(0, -1, 0)
        }

        assertThrows<IllegalArgumentException> {
            Position(0, 0, -1)
        }

        assertThrows<IllegalArgumentException> {
            Position(1, 0, 0)
        }

        assertThrows<IllegalArgumentException> {
            Position(0, 1, 0)
        }

        assertThrows<IllegalArgumentException> {
            Position(0, 3, 3)
        }
    }
}
