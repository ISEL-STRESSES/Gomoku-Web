package gomoku.server.domain.game.board

import gomoku.server.domain.game.player.Color
import gomoku.server.domain.game.player.toColor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test


class ColorTests {

    @Test
    fun `Verify Number of Colors`() {
        assertEquals(2, Color.values().size)
    }

    @Test
    fun `Verify Color other()`() {
        assertSame(Color.BLACK, Color.WHITE.other())
        assertSame(Color.WHITE, Color.BLACK.other())
    }

    @Test
    fun `Verify color indexation`() {
        var expectedColor = Color.BLACK
        (0 until 100).forEach { idx ->
            assertSame(expectedColor, idx.toColor())

            expectedColor = expectedColor.other()
        }
    }
}
