package gomoku.server.domain.game.rules

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class BoardSizeTests {

    @Test
    fun `toBoardSize deserializes valid integers to board sizes`() {
        assertEquals(BoardSize.X15, 15.toBoardSize())
        assertEquals(BoardSize.X19, 19.toBoardSize())
    }

    @Test
    fun `toBoardSize throws exception for invalid integers`() {
        assertThrows<IllegalArgumentException> {
            10.toBoardSize() // An invalid board size
        }
    }
}
