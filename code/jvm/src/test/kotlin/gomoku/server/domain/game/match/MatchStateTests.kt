package gomoku.server.domain.game.match

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MatchStateTests {

    @Test
    fun `Verify Number of MatchStates`() {
        assertEquals(3, MatchState.values().size)
    }

    @Test
    fun `toMatchState() should return the corresponding MatchState`() {
        assertEquals(MatchState.WAITING_PLAYER, "WAITING_PLAYER".toMatchState())
        assertEquals(MatchState.ONGOING, "ONGOING".toMatchState())
        assertEquals(MatchState.FINISHED, "FINISHED".toMatchState())
    }
}
