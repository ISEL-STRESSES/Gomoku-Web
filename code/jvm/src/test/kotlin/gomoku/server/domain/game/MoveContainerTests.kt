package gomoku.server.domain.game

import gomoku.server.domain.game.match.MoveContainer
import gomoku.server.domain.game.match.Position
import gomoku.server.domain.game.rules.BoardSize
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MoveContainerTests {
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
    // TODO: ADD REMAINING BOARD TESTS
}
