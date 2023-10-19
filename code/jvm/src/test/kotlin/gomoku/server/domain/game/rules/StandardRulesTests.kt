package gomoku.server.domain.game.rules

import gomoku.server.domain.game.match.Color
import gomoku.server.domain.game.match.Move
import gomoku.server.domain.game.match.MoveContainer
import gomoku.server.domain.game.match.Position
import gomoku.utils.Failure
import gomoku.utils.Success
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StandardRulesTests {
    private val rule = StandardRules(1, BoardSize.X15)

    @Test
    fun `isValidMove checks for unoccupied spots`() {
        val result = MoveContainer.buildMoveContainer(rule.boardSize.value, listOf(7))
        val moves = (result as Success).value
        assertTrue(rule.isValidMove(moves, Move(Position(8), Color.WHITE), Color.WHITE) is Success)
        assertTrue(rule.isValidMove(moves, Move(Position(7), Color.WHITE), Color.WHITE) is Failure)
    }

    @Test
    fun `isValidMove checks for alternating colors`() {
        val result = MoveContainer.buildMoveContainer(rule.boardSize.value, listOf(7))
        val moves = (result as Success).value
        assertTrue(rule.isValidMove(moves, Move(Position(8), Color.BLACK), Color.WHITE) is Failure)
    }

    @Test
    fun `possibleMoves returns unoccupied spots`() {
        val result = MoveContainer.buildMoveContainer(rule.boardSize.value, listOf(7))
        val moves = (result as Success).value
        val possible = rule.possibleMoves(moves, Color.WHITE)
        assertTrue(possible.contains(Move(Position(8), Color.WHITE)))
        assertFalse(possible.contains(Move(Position(7), Color.WHITE)))
    }

    @Test
    fun `isWinningMove detects horizontal win`() {
        val result = MoveContainer.buildMoveContainer(rule.boardSize.value, listOf(8,20,9,30,10,2,11,3))
        val moves = (result as Success).value
        assertTrue(rule.isWinningMove(moves, Move(Position(12), Color.BLACK)))
    }

    @Test
    fun `isWinningMove detects vertical win`() {
        val result = MoveContainer.buildMoveContainer(rule.boardSize.value, listOf(1,20,16,30,31,2,46,3))
        val moves = (result as Success).value
        assertTrue(rule.isWinningMove(moves, Move(Position(61), Color.BLACK)))
    }

    @Test
    fun `isWinningMove detects diagonal win (top-left to bottom-right)`() {
        val result = MoveContainer.buildMoveContainer(rule.boardSize.value, listOf(1,20,17,30,33,2,49,3))
        val moves = (result as Success).value
        assertTrue(rule.isWinningMove(moves, Move(Position(65), Color.BLACK)))
    }

    @Test
    fun `isWinningMove detects diagonal win (top-right to bottom-left)`() {
        val result = MoveContainer.buildMoveContainer(rule.boardSize.value, listOf(26,20,40,30,54,2,68,3))
        val moves = (result as Success).value
        assertTrue(rule.isWinningMove(moves, Move(Position(82), Color.BLACK)))
    }

    @Test
    fun `isWinningMove should detect more then 5 pieces in a row`() {
        val result = MoveContainer.buildMoveContainer(rule.boardSize.value, listOf(8,20,9,30,10,2,11,3,12,4))
        val moves = (result as Success).value
        assertTrue(rule.isWinningMove(moves, Move(Position(13), Color.BLACK)))
    }

    @Test
    fun `isWinningMove doesn't detect less than 5 pieces in a row`() {
        val result = MoveContainer.buildMoveContainer(rule.boardSize.value, listOf(8,20,9,30,10,2))
        val moves = (result as Success).value
        assertFalse(rule.isWinningMove(moves, Move(Position(11), Color.BLACK)))
    }

    @Test
    fun `isWinningMove doesn't detect win with gaps`() {
        val result = MoveContainer.buildMoveContainer(rule.boardSize.value, listOf(8,20,9,30,22,2,11,3,12,4))
        val moves = (result as Success).value
        assertFalse(rule.isWinningMove(moves, Move(Position(13), Color.BLACK)))
    }
}
