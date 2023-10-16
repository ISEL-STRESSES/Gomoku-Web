package gomoku.server.domain.game.player

import gomoku.server.domain.game.match.Position
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PositionTests {

    @Test
    fun `Position initializes with valid values`() {
        val position = Position(10)

        assertEquals(10, position.value)
    }

    @Test
    fun `Position throws exception for negative position`() {
        assertThrows<IllegalArgumentException> {
            Position(-1)
        }
    }
}
