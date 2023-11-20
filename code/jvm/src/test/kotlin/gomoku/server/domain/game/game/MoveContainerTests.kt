package gomoku.server.domain.game.game

import gomoku.server.domain.game.game.move.Move
import gomoku.server.domain.game.game.move.MoveContainer
import gomoku.server.domain.game.game.move.Position
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MoveContainerTests {

    @Test
    fun `addMove with move inside board and position unoccupied returns success`() {
        val boardSize = 3
        val moveContainer = MoveContainer.createEmptyMoveContainer(boardSize)
        val move = Move(Position(0, 0), CellColor.BLACK)
        val result = moveContainer.addMove(move)
        assertEquals(move, result!!.getLastMoveOrNull())
    }

    @Test
    fun `addMove with move inside board and position occupied returns null (meaning Position is already occupied)`() {
        val boardSize = 3
        var moveContainer = MoveContainer.createEmptyMoveContainer(boardSize)
        val move = Move(Position(0, 0), CellColor.WHITE)
        moveContainer = moveContainer.addMove(move)!!
        assertTrue(moveContainer.addMove(move) == null)
    }

    @Test
    fun `getLastMove with no moves returns null`() {
        val moveContainer = MoveContainer.createEmptyMoveContainer(3)
        assertNull(moveContainer.getLastMoveOrNull())
    }

    @Test
    fun `getLastMove with some moves returns the last move`() {
        val boardSize = 3
        var moveContainer = MoveContainer.createEmptyMoveContainer(boardSize)
        moveContainer = moveContainer.addMove(Move(Position(0, 0), CellColor.BLACK))!!
        moveContainer = moveContainer.addMove(Move(Position(1, 1), CellColor.WHITE))!!
        assertEquals(CellColor.WHITE, moveContainer.getLastMoveOrNull()?.cellColor)
    }

    @Test
    fun `hasMove with move returns true`() {
        val boardSize = 3
        var moveContainer = MoveContainer.createEmptyMoveContainer(boardSize)
        moveContainer = moveContainer.addMove(Move(Position(0, 0), CellColor.BLACK))!!
        assertTrue(moveContainer.hasMove(Position(0, 0)))
    }

    @Test
    fun `hasMove without move returns false`() {
        val boardSize = 3
        val moveContainer = MoveContainer.createEmptyMoveContainer(boardSize)
        assertFalse(moveContainer.hasMove(Position(0, 0)))
    }

    @Test
    fun `getMoves with no moves returns empty list`() {
        val moveContainer = MoveContainer.createEmptyMoveContainer(3)
        assertTrue(moveContainer.getMoves().isEmpty())
    }

    @Test
    fun `getMoves with some moves returns list of moves`() {
        val boardSize = 3
        var moveContainer = MoveContainer.createEmptyMoveContainer(boardSize)
        moveContainer = moveContainer.addMove(Move(Position(0, 0), CellColor.BLACK))!!
        moveContainer = moveContainer.addMove(Move(Position(0, 1), CellColor.WHITE))!!
        assertEquals(2, moveContainer.getMoves().size)
    }

    // Additional Tests
    @Test
    fun `isFull with full board returns true`() {
        val boardSize = 2
        var moveContainer = MoveContainer.createEmptyMoveContainer(boardSize) // 2x2 board
        moveContainer = moveContainer.addMove(Move(Position(0, 0), CellColor.BLACK))!!
        moveContainer = moveContainer.addMove(Move(Position(0, 1), CellColor.WHITE))!!
        moveContainer = moveContainer.addMove(Move(Position(1, 0), CellColor.BLACK))!!
        moveContainer = moveContainer.addMove(Move(Position(1, 1), CellColor.WHITE))!!
        assertTrue(moveContainer.isFull())
    }

    @Test
    fun `isFull with not full board returns false`() {
        val boardSize = 3
        var moveContainer = MoveContainer.createEmptyMoveContainer(boardSize) // 3x3 board
        moveContainer = moveContainer.addMove(Move(Position(0, 0), CellColor.BLACK))!!
        assertFalse(moveContainer.isFull())
    }

    @Test
    fun `isEmpty with empty board returns true`() {
        val moveContainer = MoveContainer.createEmptyMoveContainer(3)
        assertTrue(moveContainer.isEmpty())
    }

    @Test
    fun `isEmpty with non-empty board returns false`() {
        val boardSize = 3
        var moveContainer = MoveContainer.createEmptyMoveContainer(boardSize)
        moveContainer = moveContainer.addMove(Move(Position(0, 0), CellColor.BLACK))!!
        assertFalse(moveContainer.isEmpty())
    }
}
