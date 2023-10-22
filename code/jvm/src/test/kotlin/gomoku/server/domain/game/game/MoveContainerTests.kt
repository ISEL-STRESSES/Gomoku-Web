package gomoku.server.domain.game.game

import gomoku.server.domain.game.game.move.AddMoveError
import gomoku.server.domain.game.game.move.Move
import gomoku.server.domain.game.game.move.MoveContainer
import gomoku.server.domain.game.game.move.Position
import gomoku.server.domain.game.rules.BoardSize
import gomoku.server.successOrNull
import gomoku.utils.Failure
import gomoku.utils.Success
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MoveContainerTests {

    // Tests for isPositionInside
    @Test
    fun `isPositionInside returns true for valid positions`() {
        val moveContainer15 = MoveContainer.createEmptyMoveContainer(BoardSize.X15.value)
        assertTrue(moveContainer15.isPositionInside(Position(0)))
        assertTrue(moveContainer15.isPositionInside(Position(224)))
        val moveContainer19 = MoveContainer.createEmptyMoveContainer(BoardSize.X19.value)
        assertTrue(moveContainer19.isPositionInside(Position(0)))
        assertTrue(moveContainer19.isPositionInside(Position(360)))
    }

    @Test
    fun `isPositionInside returns false for invalid positions`() {
        val moveContainer15 = MoveContainer.createEmptyMoveContainer(BoardSize.X15.value)
        assertFalse(moveContainer15.isPositionInside(Position(225)))
        assertFalse(moveContainer15.isPositionInside(Position(230)))
        val moveContainer19 = MoveContainer.createEmptyMoveContainer(BoardSize.X19.value)
        assertFalse(moveContainer19.isPositionInside(Position(361)))
        assertFalse(moveContainer19.isPositionInside(Position(366)))
    }

    @Test
    fun `addMove with move inside board and position unoccupied returns success`() {
        val moveContainer = MoveContainer.createEmptyMoveContainer(3)
        val move = Move(Position(0), Color.BLACK)
        val result = moveContainer.addMove(move)
        assertTrue(result is Success)
        assertEquals(move, result.value.getLastMoveOrNull())
    }

    @Test
    fun `addMove with move outside board returns failure with ImpossiblePosition error`() {
        val moveContainer = MoveContainer.createEmptyMoveContainer(3)
        val move = Move(Position(10), Color.BLACK)
        val result = moveContainer.addMove(move)
        assertTrue(result is Failure && result.value == AddMoveError.ImpossiblePosition)
    }

    @Test
    fun `addMove with move inside board and position occupied returns failure with AlreadyOccupied error`() {
        var moveContainer = MoveContainer.createEmptyMoveContainer(3)
        val move = Move(Position(0), Color.WHITE)
        moveContainer = moveContainer.addMove(move).successOrNull()!!
        val result = moveContainer.addMove(move)
        assertTrue(result is Failure && result.value == AddMoveError.AlreadyOccupied)
    }

    @Test
    fun `getLastMove with no moves returns null`() {
        val moveContainer = MoveContainer.createEmptyMoveContainer(3)
        assertNull(moveContainer.getLastMoveOrNull())
    }

    @Test
    fun `getLastMove with some moves returns the last move`() {
        var moveContainer = MoveContainer.createEmptyMoveContainer(3)
        moveContainer = moveContainer.addMove(Move(Position(0), Color.BLACK)).successOrNull()!!
        moveContainer = moveContainer.addMove(Move(Position(1), Color.WHITE)).successOrNull()!!
        assertEquals(Color.WHITE, moveContainer.getLastMoveOrNull()?.color)
    }

    @Test
    fun `hasMove with move returns true`() {
        var moveContainer = MoveContainer.createEmptyMoveContainer(3)
        moveContainer = moveContainer.addMove(Move(Position(0), Color.BLACK)).successOrNull()!!
        assertTrue(moveContainer.hasMove(Position(0)))
    }

    @Test
    fun `hasMove without move returns false`() {
        val moveContainer = MoveContainer.createEmptyMoveContainer(3)
        assertFalse(moveContainer.hasMove(Position(0)))
    }

    @Test
    fun `getMoves with no moves returns empty list`() {
        val moveContainer = MoveContainer.createEmptyMoveContainer(3)
        assertTrue(moveContainer.getMoves().isEmpty())
    }

    @Test
    fun `getMoves with some moves returns list of moves`() {
        var moveContainer = MoveContainer.createEmptyMoveContainer(3)
        moveContainer = moveContainer.addMove(Move(Position(0), Color.BLACK)).successOrNull()!!
        moveContainer = moveContainer.addMove(Move(Position(1), Color.WHITE)).successOrNull()!!
        assertEquals(2, moveContainer.getMoves().size)
    }

    // Additional Tests
    @Test
    fun `isFull with full board returns true`() {
        var moveContainer = MoveContainer.createEmptyMoveContainer(2) // 2x2 board
        moveContainer = moveContainer.addMove(Move(Position(0), Color.BLACK)).successOrNull()!!
        moveContainer = moveContainer.addMove(Move(Position(1), Color.WHITE)).successOrNull()!!
        moveContainer = moveContainer.addMove(Move(Position(2), Color.BLACK)).successOrNull()!!
        moveContainer = moveContainer.addMove(Move(Position(3), Color.WHITE)).successOrNull()!!
        assertTrue(moveContainer.isFull())
    }

    @Test
    fun `isFull with not full board returns false`() {
        var moveContainer = MoveContainer.createEmptyMoveContainer(3) // 3x3 board
        moveContainer = moveContainer.addMove(Move(Position(0), Color.BLACK)).successOrNull()!!
        moveContainer = moveContainer.addMove(Move(Position(1), Color.WHITE)).successOrNull()!!
        assertFalse(moveContainer.isFull())
    }

    @Test
    fun `isEmpty with empty board returns true`() {
        val moveContainer = MoveContainer.createEmptyMoveContainer(3)
        assertTrue(moveContainer.isEmpty())
    }

    @Test
    fun `isEmpty with non-empty board returns false`() {
        var moveContainer = MoveContainer.createEmptyMoveContainer(3)
        moveContainer = moveContainer.addMove(Move(Position(0), Color.BLACK)).successOrNull()!!
        assertFalse(moveContainer.isEmpty())
    }
}
