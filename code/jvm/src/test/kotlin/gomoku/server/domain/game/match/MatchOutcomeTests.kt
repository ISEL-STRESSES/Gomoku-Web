package gomoku.server.domain.game.match

import gomoku.server.domain.game.player.Color
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MatchOutcomeTests {

    @Test
    fun `Verify Number of MatchOutcomes`() {
        assertEquals(3, MatchOutcome.values().size)
    }

    @Test
    fun `Verify winner color`() {
        assertEquals(Color.BLACK, MatchOutcome.BLACK_WON.winnerColor)
        assertEquals(Color.WHITE, MatchOutcome.WHITE_WON.winnerColor)
    }

    @Test
    fun `Verify no winner color`() {
        assertEquals(null, MatchOutcome.DRAW.winnerColor)
    }

}