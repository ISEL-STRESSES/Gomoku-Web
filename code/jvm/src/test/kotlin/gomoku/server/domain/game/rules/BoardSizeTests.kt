package gomoku.server.domain.game.rules

import gomoku.server.domain.game.player.Position
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class BoardSizeTests {

    @Test
    fun `isPositionInside returns true for valid positions`() {
        assertTrue(BoardSize.X15.isPositionInside(Position(0, 0)))
        assertTrue(BoardSize.X15.isPositionInside(Position(14, 14)))
        assertTrue(BoardSize.X19.isPositionInside(Position(0, 0)))
        assertTrue(BoardSize.X19.isPositionInside(Position(18, 18)))
    }

    @Test
    fun `isPositionInside returns false for invalid positions`() {
        assertFalse(BoardSize.X15.isPositionInside(Position(15, 0)))
        assertFalse(BoardSize.X15.isPositionInside(Position(0, 15)))
        assertFalse(BoardSize.X19.isPositionInside(Position(19, 0)))
        assertFalse(BoardSize.X19.isPositionInside(Position(0, 19)))
    }

    @Test
    fun `getAllPositions returns all valid positions`() {
        assertEquals(15*15, BoardSize.X15.getAllPositions().size)
        assertEquals(19*19, BoardSize.X19.getAllPositions().size)
        assertTrue(BoardSize.X15.getAllPositions().contains(Position(0, 0)))
        assertTrue(BoardSize.X19.getAllPositions().contains(Position(18, 18)))
    }

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
