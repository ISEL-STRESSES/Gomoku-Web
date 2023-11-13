package gomoku.server.domain.game.rules

import gomoku.server.domain.game.game.CellColor
import gomoku.server.domain.game.game.move.Move
import gomoku.server.domain.game.game.move.MoveContainer
import gomoku.server.domain.game.game.move.Position
import gomoku.utils.Failure
import gomoku.utils.Success
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class StandardRulesTests {
    private val rule = StandardRules(1, BoardSize.X15)

    @Test
    fun `isValidMove checks for unoccupied spots`() {
        val result = MoveContainer.buildMoveContainer(rule.boardSize.value, listOf(7))
        assertNotNull(result)
        assertTrue(rule.isValidMove(result, Move(Position(8, 4, 200), CellColor.WHITE), CellColor.WHITE) is Success)
        assertTrue(rule.isValidMove(result, Move(Position(7,0,200), CellColor.WHITE), CellColor.WHITE) is Failure)
    }

    @Test
    fun `isValidMove checks for alternating colors`() {
        val result = MoveContainer.buildMoveContainer(rule.boardSize.value, listOf(7))
        assertNotNull(result)
        assertTrue(rule.isValidMove(result, Move(Position(8,1,200), CellColor.BLACK), CellColor.WHITE) is Failure)
    }

    @Test
    fun `possibleMoves returns unoccupied spots`() {
        val result = MoveContainer.buildMoveContainer(rule.boardSize.value, listOf(7))
        assertNotNull(result)
        val possible = rule.possiblePositions(result, CellColor.WHITE, CellColor.BLACK)
        assertTrue(possible.contains(Position(8, 4, 200)))
        assertFalse(possible.contains(Position(7, 0, 200)))
    }

    @Test
    fun `isWinningMove detects horizontal win`() {
        val result = MoveContainer.buildMoveContainer(rule.boardSize.value, listOf(8, 20, 9, 30, 10, 2, 11, 3))
        assertNotNull(result)
        assertTrue(rule.isWinningMove(result, Move(Position(12,0,200), CellColor.BLACK)))
    }

    @Test
    fun `isWinningMove detects vertical win`() {
        val result = MoveContainer.buildMoveContainer(rule.boardSize.value, listOf(1, 20, 16, 30, 31, 2, 46, 3))
        assertNotNull(result)
        assertTrue(rule.isWinningMove(result, Move(Position(61,0,200), CellColor.BLACK)))
    }

    @Test
    fun `isWinningMove detects diagonal win (top-left to bottom-right)`() {
        val result = MoveContainer.buildMoveContainer(rule.boardSize.value, listOf(1, 20, 17, 30, 33, 2, 49, 3))
        assertNotNull(result)
        assertTrue(rule.isWinningMove(result, Move(Position(65,23,200), CellColor.BLACK)))
    }

    @Test
    fun `isWinningMove detects diagonal win (top-right to bottom-left)`() {
        val result = MoveContainer.buildMoveContainer(rule.boardSize.value, listOf(26, 20, 40, 30, 54, 2, 68, 3))
        assertNotNull(result)
        assertTrue(rule.isWinningMove(result, Move(Position(82, 2, 200), CellColor.BLACK)))
    }

    @Test
    fun `isWinningMove should detect more then 5 pieces in a row`() {
        val result = MoveContainer.buildMoveContainer(rule.boardSize.value, listOf(8, 20, 9, 30, 10, 2, 11, 3, 12, 4))
        assertNotNull(result)
        assertTrue(rule.isWinningMove(result, Move(Position(13, 2, 200), CellColor.BLACK)))
    }

    @Test
    fun `isWinningMove doesn't detect less than 5 pieces in a row`() {
        val result = MoveContainer.buildMoveContainer(rule.boardSize.value, listOf(8, 20, 9, 30, 10, 2))
        assertNotNull(result)
        assertFalse(rule.isWinningMove(result, Move(Position(11, 2, 200), CellColor.BLACK)))
    }

    @Test
    fun `isWinningMove doesn't detect win with gaps`() {
        val result = MoveContainer.buildMoveContainer(rule.boardSize.value, listOf(8, 20, 9, 30, 22, 2, 11, 3, 12, 4))
        assertNotNull(result)
        assertFalse(rule.isWinningMove(result, Move(Position(13,10, 200), CellColor.BLACK)))
    }
}
