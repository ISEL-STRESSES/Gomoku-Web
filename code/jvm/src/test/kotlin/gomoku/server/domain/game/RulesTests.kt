package gomoku.server.domain.game

import gomoku.server.domain.game.board.Position
import org.junit.jupiter.api.Assertions

class RulesTests {
    fun `Test Board Sizes`() {
        BoardSize.values().forEach { size ->
            val maxSize = size.max
            Assertions.assertTrue(size.isPositionInside(Position(0, 0)))
            Assertions.assertTrue(size.isPositionInside(Position(maxSize - 1, maxSize - 1)))
            Assertions.assertFalse(size.isPositionInside(Position(maxSize, maxSize)))
        }
    }
}
