package gomoku.server.domain.game.player

import gomoku.server.domain.game.game.CellColor
import gomoku.server.domain.game.game.toColor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test

class CellColorTests {

    @Test
    fun `Verify Number of Colors`() {
        assertEquals(2, CellColor.values().size)
    }

    @Test
    fun `Verify Color other()`() {
        assertSame(CellColor.BLACK, CellColor.WHITE.other())
        assertSame(CellColor.WHITE, CellColor.BLACK.other())
    }

    @Test
    fun `Verify color indexation`() {
        var expectedCellColor = CellColor.BLACK
        (0 until 100).forEach { idx ->
            assertSame(expectedCellColor, idx.toColor())

            expectedCellColor = expectedCellColor.other()
        }
    }
}
