package gomoku.server.domain.game.player

import gomoku.server.domain.game.game.move.Position
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PositionTests {

    @Test
    fun `Position initializes with valid values`() {
        val position = Position(10, 10)

        assertEquals(10, position.x)
        assertEquals(10, position.y)
    }
}
