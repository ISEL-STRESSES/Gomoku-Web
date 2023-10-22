package gomoku.server.domain.game.game

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GameOutcomeTests {

    @Test
    fun `Verify Number of GameOutcomes`() {
        assertEquals(3, GameOutcome.values().size)
    }

    @Test
    fun `Verify winner color`() {
        assertEquals(Color.BLACK, GameOutcome.BLACK_WON.winnerColor)
        assertEquals(Color.WHITE, GameOutcome.WHITE_WON.winnerColor)
    }

    @Test
    fun `Verify no winner color`() {
        assertEquals(null, GameOutcome.DRAW.winnerColor)
    }
}
