package gomoku.server.domain.game.match

import gomoku.server.domain.game.rules.BoardSize
import gomoku.server.domain.game.rules.StandardRules
import gomoku.utils.Failure
import gomoku.utils.Success
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class MatchDomainTests {

    private val playerA = 1
    private val playerB = 4
    private val rules = StandardRules(1, BoardSize.X15)
    private val moves: MoveContainer = MoveContainer.createEmptyMoveContainer(rules.boardSize.value)

    @Test
    fun `OngoingMatch computes correct turn color`() {
        val newContainerWithMoves1 = moves.addMove(Move(Position(1), Color.BLACK))
        if (newContainerWithMoves1 is Success) {
            val ongoingMatch1 = OngoingMatch(1, playerA, playerB, rules, newContainerWithMoves1.value)
            assertEquals(Color.WHITE, ongoingMatch1.turn)
            val newContainerWithMoves2 = newContainerWithMoves1.value.addMove(Move(Position(2), Color.WHITE))
            if (newContainerWithMoves2 is Success) {
                val ongoingMatch2 = OngoingMatch(1, playerA, playerB, rules, newContainerWithMoves2.value)
                assertEquals(Color.BLACK, ongoingMatch2.turn)
            } else {
                assert(false)
            }
        } else {
            assert(false)
        }

        val newContainerWithMovesFail = moves.addMove(Move(Position(225), Color.BLACK))
        if (newContainerWithMovesFail is Failure) {
            assert(true)
        } else {
            assert(false)
        }
    }

    @Test
    fun `FinishedGame getWinnerOrNull() returns correct winner`() {
        val matchOutcomeWinnerA = MatchOutcome.BLACK_WON
        val finishedMatchA = FinishedMatch(1, playerA, playerB, rules, moves, matchOutcomeWinnerA)

        assertEquals(playerA, finishedMatchA.getWinnerIdOrNull())

        val matchOutcomeWinnerB = MatchOutcome.WHITE_WON
        val finishedMatchB = FinishedMatch(1, playerA, playerB, rules, moves, matchOutcomeWinnerB)

        assertEquals(playerB, finishedMatchB.getWinnerIdOrNull())
    }

    @Test
    fun `FinishedGame getWinnerOrNull() returns null for draw`() {
        val matchOutcomeNoWinner = MatchOutcome.DRAW
        val finishedMatch = FinishedMatch(1, playerA, playerB, rules, moves, matchOutcomeNoWinner)

        assertNull(finishedMatch.getWinnerIdOrNull())
    }
}
