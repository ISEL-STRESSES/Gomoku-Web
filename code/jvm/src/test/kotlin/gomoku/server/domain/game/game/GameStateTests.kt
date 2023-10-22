package gomoku.server.domain.game.game

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GameStateTests {

    @Test
    fun `Verify Number of GameStates`() {
        assertEquals(2, GameState.values().size)
    }

    @Test
    fun `toGameState() should return the corresponding GameState`() {
        assertEquals(GameState.ONGOING, "ONGOING".toGameState())
        assertEquals(GameState.FINISHED, "FINISHED".toGameState())
    }
}
