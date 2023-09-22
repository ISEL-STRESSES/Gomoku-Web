package gomoku.server.domain.game.board

import gomoku.server.domain.game.InvalidBoardException
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.assertThrows

class BoardTests {

    fun `Build Empty Board`() {
        val board = emptyList<Position>().toBoard().getOrThrow()

        assert(board.grid.isEmpty())
        assert(board.at(Position(0, 0)) == null)
    }

    fun `Build Board with positions`() {
        val positions = listOf(
            Position(0, 0),
            Position(1, 0),
            Position(2, 0),
            Position(3, 0),
        )

        val board = positions.toBoard().getOrThrow()

        positions.forEachIndexed { idx, pos ->
            val color = board.at(pos)
            assertSame(idx.toColor(), color)
        }
    }

    fun `Build Invalid board with repeated moves`() {
        val positions = listOf(
            Position(0, 0),
            Position(0, 0),
        )

        assertThrows<InvalidBoardException> {
            positions.toBoard().getOrThrow()
        }
    }
}