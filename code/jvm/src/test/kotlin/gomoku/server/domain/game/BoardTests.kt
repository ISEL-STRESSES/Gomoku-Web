package gomoku.server.domain.game

import gomoku.server.domain.game.player.Position
import gomoku.server.domain.game.rules.BoardSize
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BoardTests {
    @Test
    fun `isPositionInside returns true for valid positions`() {
        val board15 = Board.createEmptyBoard(BoardSize.X15.value)
        assertTrue(board15.isPositionInside(Position(0, 0)))
        assertTrue(board15.isPositionInside(Position(14, 14)))
        val board19 = Board.createEmptyBoard(BoardSize.X19.value)
        assertTrue(board19.isPositionInside(Position(0, 0)))
        assertTrue(board19.isPositionInside(Position(18, 18)))
    }

    @Test
    fun `isPositionInside returns false for invalid positions`() {
        val board15 = Board.createEmptyBoard(BoardSize.X15.value)
        assertFalse(board15.isPositionInside(Position(15, 0)))
        assertFalse(board15.isPositionInside(Position(0, 15)))
        val board19 = Board.createEmptyBoard(BoardSize.X19.value)
        assertFalse(board19.isPositionInside(Position(19, 0)))
        assertFalse(board19.isPositionInside(Position(0, 19)))
    }
    // TODO: ADD REMAINING BOARD TESTS
}
