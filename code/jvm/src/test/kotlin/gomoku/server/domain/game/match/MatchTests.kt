package gomoku.server.domain.game.match

import gomoku.server.domain.game.player.Color
import gomoku.server.domain.game.player.Move
import gomoku.server.domain.game.player.Player
import gomoku.server.domain.game.player.Position
import gomoku.server.domain.game.rules.BoardSize
import gomoku.server.domain.game.rules.StandardRules
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class MatchTests {

    private val playerA = Player(1, Color.BLACK)
    private val playerB = Player(2, Color.WHITE)
    private val rules = StandardRules(BoardSize.X15)
    private val moves : List<Move> = listOf()

    @Test
    fun `getPlayerByColor returns correct player for given color in ongoing and finished match`() {
        val match = OngoingMatch(1, playerA, playerB, rules, moves)

        assertEquals(playerA, match.getPlayerByColor(playerA.color))
        assertEquals(playerB, match.getPlayerByColor(playerB.color))

        val match2 = FinishedGame(1, playerA, playerB, rules, moves, MatchOutcome.BLACK_WON)

        assertEquals(playerA, match2.getPlayerByColor(playerA.color))
        assertEquals(playerB, match2.getPlayerByColor(playerB.color))
    }

    @Test
    fun `OngoingMatch computes correct turn color`() {
        val movesWithEvenSize = listOf(Move(Position(1, 1), Color.BLACK), Move(Position(1, 2), Color.WHITE)) // Size = 2
        val ongoingMatchEven = OngoingMatch(1, playerA, playerB, rules, movesWithEvenSize)
        assertEquals(Color.BLACK, ongoingMatchEven.turn)

        val movesWithOddSize = listOf(Move(Position(1, 1), Color.BLACK)) // Size = 1
        val ongoingMatchOdd = OngoingMatch(1, playerA, playerB, rules, movesWithOddSize)
        assertEquals(Color.WHITE, ongoingMatchOdd.turn)
    }

    @Test
    fun `FinishedGame getWinnerOrNull() returns correct winner`() {
        val matchOutcomeWinnerA = MatchOutcome.BLACK_WON
        val finishedGameA = FinishedGame(1, playerA, playerB, rules, moves, matchOutcomeWinnerA)

        assertEquals(playerA, finishedGameA.getWinnerOrNull())

        val matchOutcomeWinnerB = MatchOutcome.WHITE_WON
        val finishedGameB = FinishedGame(1, playerA, playerB, rules, moves, matchOutcomeWinnerB)

        assertEquals(playerB, finishedGameB.getWinnerOrNull())
    }

    @Test
    fun `FinishedGame getWinnerOrNull() returns null for draw`() {
        val matchOutcomeNoWinner = MatchOutcome.DRAW
        val finishedGame = FinishedGame(1, playerA, playerB, rules, moves, matchOutcomeNoWinner)

        assertNull(finishedGame.getWinnerOrNull())
    }
}
