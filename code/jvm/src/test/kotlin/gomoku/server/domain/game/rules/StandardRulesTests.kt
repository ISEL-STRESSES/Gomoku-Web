package gomoku.server.domain.game.rules

import gomoku.server.domain.game.player.Color
import gomoku.server.domain.game.player.Move
import gomoku.server.domain.game.player.Position
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class StandardRulesTest {

    private val rule = StandardRules(BoardSize.X15)

    @Test
    fun `isValidMove checks for unoccupied spots`() {
        val moves = listOf(Move(Position(7, 7), Color.BLACK))
        assertTrue(rule.isValidMove(moves, Move(Position(8, 8), Color.WHITE)))
        assertFalse(rule.isValidMove(moves, Move(Position(7, 7), Color.WHITE)))
    }

    @Test
    fun `isValidMove checks for alternating colors`() {
        val moves = listOf(Move(Position(7, 7), Color.BLACK))
        assertFalse(rule.isValidMove(moves, Move(Position(8, 8), Color.BLACK)))
    }

    @Test
    fun `possibleMoves returns unoccupied spots`() {
        val moves = listOf(Move(Position(7, 7), Color.BLACK))
        val possible = rule.possibleMoves(moves, Color.WHITE)
        assertTrue(possible.contains(Move(Position(8, 8), Color.WHITE)))
        assertFalse(possible.contains(Move(Position(7, 7), Color.WHITE)))
    }

    @Test
    fun `isWinningMove detects horizontal win`() {
        val moves = listOf(
            Move(Position(8, 7), Color.BLACK),
            Move(Position(9, 7), Color.BLACK),
            Move(Position(10, 7), Color.BLACK),
            Move(Position(11, 7), Color.BLACK)
        )
        assertTrue(rule.isWinningMove(moves, Move(Position(12, 7), Color.BLACK)))
    }

    @Test
    fun `isWinningMove detects vertical win`() {
        val moves = listOf(
            Move(Position(7, 8), Color.BLACK),
            Move(Position(7, 9), Color.BLACK),
            Move(Position(7, 10), Color.BLACK),
            Move(Position(7, 11), Color.BLACK)
        )
        assertTrue(rule.isWinningMove(moves, Move(Position(7, 12), Color.BLACK)))
    }

    @Test
    fun `isWinningMove detects diagonal win (top-left to bottom-right)`() {
        val moves = listOf(
            Move(Position(8, 8), Color.BLACK),
            Move(Position(9, 9), Color.BLACK),
            Move(Position(10, 10), Color.BLACK),
            Move(Position(11, 11), Color.BLACK)
        )
        assertTrue(rule.isWinningMove(moves, Move(Position(12, 12), Color.BLACK)))
    }

    @Test
    fun `isWinningMove detects diagonal win (top-right to bottom-left)`() {
        val moves = listOf(
            Move(Position(11, 8), Color.BLACK),
            Move(Position(10, 9), Color.BLACK),
            Move(Position(9, 10), Color.BLACK),
            Move(Position(8, 11), Color.BLACK)
        )
        assertTrue(rule.isWinningMove(moves, Move(Position(7, 12), Color.BLACK)))
    }

    @Test
    fun `isWinningMove should detect more then 5 pieces in a row`() {
        val moves = listOf(
            Move(Position(7, 7), Color.BLACK),
            Move(Position(8, 8), Color.BLACK),
            Move(Position(9, 9), Color.BLACK),
            Move(Position(10, 10), Color.BLACK),
            Move(Position(11, 11), Color.BLACK),
        )
        assertTrue(rule.isWinningMove(moves, Move(Position(12, 12), Color.BLACK)))
    }

    @Test
    fun `isWinningMove doesn't detect less than 5 pieces in a row`() {
        val moves = listOf(
            Move(Position(8, 8), Color.BLACK),
            Move(Position(9, 9), Color.BLACK),
            Move(Position(10, 10), Color.BLACK)
        )
        assertFalse(rule.isWinningMove(moves, Move(Position(11, 11), Color.BLACK)))
    }

    @Test
    fun `isWinningMove doesn't detect win with gaps`() {
        val moves = listOf(
            Move(Position(7, 7), Color.BLACK),
            Move(Position(8, 8), Color.BLACK),
            Move(Position(10, 10), Color.BLACK),
            Move(Position(11, 11), Color.BLACK),
            Move(Position(12, 12), Color.BLACK)
        )
        assertFalse(rule.isWinningMove(moves, Move(Position(13, 13), Color.BLACK)))
    }

}
