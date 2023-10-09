package gomoku.server.domain.game.board

import gomoku.server.domain.game.player.Color
import gomoku.server.domain.game.player.toColor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame

class ColorTests {
    fun `Verify Number of Colors`() {
        assertEquals(2, Color.values().size)
    }

    fun `Verify Color other()`() {
        assertSame(Color.BLACK, Color.WHITE.other())
        assertSame(Color.WHITE, Color.BLACK.other())
    }

    fun `Verify color indexation`() {
        var expectedColor = Color.BLACK
        (0 until 100).forEach { idx ->
            assertSame(expectedColor, idx.toColor())

            expectedColor = expectedColor.other()
        }
    }
}
