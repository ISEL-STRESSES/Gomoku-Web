package gomoku.server.domain.game.game

import gomoku.server.domain.game.game.move.Move
import gomoku.server.domain.game.game.move.MoveContainer
import gomoku.server.domain.game.game.move.Position
import gomoku.server.domain.game.rules.BoardSize
import gomoku.server.domain.game.rules.StandardRules
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class GameDomainTests {

    private val playerA = 1
    private val playerB = 4
    private val rules = StandardRules(1, BoardSize.X15)
    private val moves: MoveContainer = MoveContainer.createEmptyMoveContainer(rules.boardSize.value)

    @Test
    fun `OngoingGame computes correct turn color`() {
        val newContainerWithMoves1: MoveContainer =
            moves.addMove(Move(Position(1, 1), CellColor.BLACK)) ?: throw AssertionError()
        val ongoingGame1 = OngoingGame(1, playerA, playerB, rules, newContainerWithMoves1)
        assertEquals(CellColor.WHITE, ongoingGame1.turn)
        val newContainerWithMoves2 =
            newContainerWithMoves1.addMove(Move(Position(2, 2), CellColor.WHITE)) ?: throw AssertionError()
        val ongoingGame2 = OngoingGame(1, playerA, playerB, rules, newContainerWithMoves2)
        assertEquals(CellColor.BLACK, ongoingGame2.turn)

    }

    @Test
    fun `FinishedGame getWinnerOrNull() returns correct winner`() {
        val gameOutcomeWinnerA = GameOutcome.BLACK_WON
        val finishedGameA = FinishedGame(1, playerA, playerB, rules, moves, gameOutcomeWinnerA)

        assertEquals(playerA, finishedGameA.getWinnerIdOrNull())

        val gameOutcomeWinnerB = GameOutcome.WHITE_WON
        val finishedGameB = FinishedGame(1, playerA, playerB, rules, moves, gameOutcomeWinnerB)

        assertEquals(playerB, finishedGameB.getWinnerIdOrNull())
    }

    @Test
    fun `FinishedGame getWinnerOrNull() returns null for draw`() {
        val gameOutcomeNoWinner = GameOutcome.DRAW
        val finishedGame = FinishedGame(1, playerA, playerB, rules, moves, gameOutcomeNoWinner)

        assertNull(finishedGame.getWinnerIdOrNull())
    }
}
