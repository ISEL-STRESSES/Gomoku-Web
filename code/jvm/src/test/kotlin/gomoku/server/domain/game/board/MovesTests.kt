package gomoku.server.domain.game.board

import org.junit.jupiter.api.Assertions.assertSame

class MovesTests {

    fun `Test Next Color Turn`() {
        assertSame(Color.BLACK, emptyList<Position>().nextColorTurn())
        assertSame(Color.WHITE, listOf(Position(1, 1)).nextColorTurn())
    }
}