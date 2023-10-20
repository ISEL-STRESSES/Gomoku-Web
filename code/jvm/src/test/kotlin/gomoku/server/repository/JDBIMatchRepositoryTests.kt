package gomoku.server.repository

import gomoku.server.domain.game.match.Color
import gomoku.server.domain.game.match.MatchOutcome
import gomoku.server.domain.game.match.MatchState
import gomoku.server.domain.game.match.Move
import gomoku.server.domain.game.match.OngoingMatch
import gomoku.server.domain.game.match.Position
import gomoku.server.domain.game.rules.BoardSize
import gomoku.server.domain.game.rules.OpeningRule
import gomoku.server.domain.game.rules.RuleVariant
import gomoku.server.repository.match.JDBIMatchRepository
import gomoku.server.repository.match.MatchRepository
import gomoku.server.repository.user.JDBIUserRepository
import gomoku.server.testWithHandleAndRollback
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class JDBIMatchRepositoryTests {

    @Test
    fun `createMatch persists new match correctly`() = testWithHandleAndRollback { handle ->
        val repo = JDBIMatchRepository(handle)

        val matchId = repo.createMatch(1, 1, 2)
        val match = repo.getMatchById(matchId)

        assertNotNull(match)
        assertEquals(1, match.rules.ruleId)
        assertTrue { match is OngoingMatch }
    }

    @Test
    fun `setMatchState updates state correctly`() = testWithHandleAndRollback { handle ->
        val repo = JDBIMatchRepository(handle)

        val matchId = repo.createMatch(1, 1, 2)
        repo.setMatchState(matchId, MatchState.FINISHED)

        val state = repo.getMatchState(matchId)
        assertEquals(MatchState.FINISHED, state)
    }

    @Test
    fun `setMatchOutcome updates outcome correctly (after game state is finished)`() = testWithHandleAndRollback { handle ->
        val repo = JDBIMatchRepository(handle)

        val matchId = repo.createMatch(1, 1, 2)
        repo.setMatchState(matchId, MatchState.FINISHED)
        repo.setMatchOutcome(matchId, MatchOutcome.BLACK_WON)

        val outcome = repo.getMatchOutcome(matchId)
        assertEquals(MatchOutcome.BLACK_WON, outcome)
    }

    @Test
    fun `setMatchOutcome doesn't update outcome if game state is not finished`() = testWithHandleAndRollback { handle ->
        val repo = JDBIMatchRepository(handle)

        val matchId = repo.createMatch(1, 1, 2)
        repo.setMatchOutcome(matchId, MatchOutcome.BLACK_WON)

        val outcome = repo.getMatchOutcome(matchId)
        assertEquals(null, outcome)
    }

    @Test
    fun `getMatchRule retrieves correct rule`() = testWithHandleAndRollback { handle ->
        val repo = JDBIMatchRepository(handle)

        val rule = repo.getRuleById(1)
        val matchId = repo.createMatch(rule?.ruleId ?: 0, 1, 2)
        val matchRule = repo.getMatchRule(matchId)

        assertEquals(rule, matchRule)
    }

    @Test
    fun `getMatchPlayers retrieves correct players`() = testWithHandleAndRollback { handle ->
        val repo = JDBIMatchRepository(handle)

        val matchId = repo.createMatch(1, 1, 2)
        val players = repo.getMatchPlayers(matchId)

        assertEquals(Pair(1, 2), players)
    }

    @Test
    fun `addToMoveArray appends move correctly`() = testWithHandleAndRollback { handle ->
        val repo = JDBIMatchRepository(handle)

        val matchId = repo.createMatch(1, 1, 2)
        repo.addToMoveArray(matchId, 3)

        val moves = repo.getAllMoves(matchId)
        assertTrue(moves.contains(Move(Position(3), Color.BLACK))) // Assuming Move has a constructor like this
    }

    @Test
    fun `getAllMoves retrieves all moves`() = testWithHandleAndRollback { handle ->
        val repo = JDBIMatchRepository(handle)

        val matchId = repo.createMatch(1, 1, 2)
        repo.addToMoveArray(matchId, 3)
        repo.addToMoveArray(matchId, 4)

        val moves = repo.getAllMoves(matchId)
        assertEquals(2, moves.size)
    }

    @Test
    fun `getTurn retrieves correct turn`() = testWithHandleAndRollback { handle ->
        val repo = JDBIMatchRepository(handle)

        val matchId = repo.createMatch(1, 1, 2)
        repo.addToMoveArray(matchId, 3)

        val turn = repo.getTurn(matchId)
        assertEquals(Color.WHITE, turn) // Assuming after 1 move by black, it's white's turn
    }

    @Test
    fun `getRuleById gets correctly and doesn't get non-existing, and getAllRules`() = testWithHandleAndRollback { handle ->

        val repo = JDBIMatchRepository(handle)

        val rule1 = repo.getRuleById(1)
        assertNotNull(rule1)

        val rule4 = repo.getRuleById(4)
        assertNull(rule4)

        val rules = repo.getAllRules()
        println(rules)
        assertNotNull(rules)
        assertEquals(3, rules.size)
    }

    @Test
    fun `check base match flow`() = testWithHandleAndRollback { handle ->

        val repo = JDBIMatchRepository(handle)
        val uRepo = JDBIUserRepository(handle)

        val matchIdNull = repo.getMatchById(31)
        assertNull(matchIdNull)

        val player1 = uRepo.getUserById(1)
        assertNotNull(player1)
        val player2 = uRepo.getUserById(2)
        assertNotNull(player2)

        val matchId = repo.createMatch(1, player1.uuid, player2.uuid)

        val matchState = repo.getMatchState(matchId)
        assertEquals(MatchState.ONGOING, matchState)

        val matchOutcome = repo.getMatchOutcome(matchId)
        assertNull(matchOutcome)
        repo.setMatchOutcome(matchId, MatchOutcome.BLACK_WON)
        val nullMatchOutcome = repo.getMatchOutcome(matchId)
        assertNull(nullMatchOutcome)

        repo.setMatchState(matchId, MatchState.FINISHED)
        val newMatchState = repo.getMatchState(matchId)
        assertEquals(MatchState.FINISHED, newMatchState)

        repo.setMatchOutcome(matchId, MatchOutcome.BLACK_WON)
        val newMatchOutcome = repo.getMatchOutcome(matchId)
        assertNotNull(newMatchOutcome)
        assertEquals(MatchOutcome.BLACK_WON, newMatchOutcome)

        val matchRule = repo.getMatchRule(matchId)
        assertNotNull(matchRule)
        assertEquals(1, matchRule.ruleId)
        assertEquals(BoardSize.X15, matchRule.boardSize)
        assertEquals(OpeningRule.FREE, matchRule.openingRule)
        assertEquals(RuleVariant.STANDARD, matchRule.variant)

        val matchPlayers = repo.getMatchPlayers(matchId)
        assertNotNull(matchPlayers)
        assertEquals(1, matchPlayers.first)
        assertEquals(2, matchPlayers.second)
    }

    @Test
    fun `make moves and get moves`() = testWithHandleAndRollback { handle ->
        val repo = JDBIMatchRepository(handle)

        val moves1 = repo.getAllMoves(6)
        assertEquals(2, moves1.size)

        val makeMove = repo.addToMoveArray(6, 3)
        assertTrue(makeMove)

        val moves2 = repo.getAllMoves(6)
        assertEquals(3, moves2.size)

        val getTurn = repo.getTurn(6)
        assertEquals(Color.WHITE, getTurn)

        val getLastNMoves = repo.getLastNMoves(6, 1)
        assertEquals(1, getLastNMoves.size)
        assertEquals(Color.BLACK, getLastNMoves[0].color)
        assertEquals(Position(3), getLastNMoves[0].position)
    }

    @Test
    fun `isRuleStoredById returns true if rule exists`() = testWithHandleAndRollback { handle ->
        val repo = JDBIMatchRepository(handle)

        val ruleExists = repo.isRuleStoredById(1)
        assertTrue(ruleExists)
    }

    @Test
    fun `isRuleStoredById returns false if rule doesn't exist`() = testWithHandleAndRollback { handle ->
        val repo = JDBIMatchRepository(handle)

        val ruleExists = repo.isRuleStoredById(666)
        assertFalse(ruleExists)
    }

    @Test
    fun `getUserFinishedMatches returns only finished matches for user`() = testWithHandleAndRollback { handle ->
        val repo = JDBIMatchRepository(handle)

        val userId = 1
        val otherPlayerId = 2
        val anotherPlayerId = 3

        val finishedMatchId1 = repo.createFinishedMatch(userId, otherPlayerId)
        val finishedMatchId2 = repo.createFinishedMatch(userId, anotherPlayerId)
        val ongoingMatchId = repo.createMatch(1, userId, otherPlayerId)

        val matches = repo.getUserFinishedMatches(0, 10, userId)

        assertTrue(matches.any { it.id == finishedMatchId1 })
        assertTrue(matches.any { it.id == finishedMatchId2 })
        assertFalse(matches.any { it.id == ongoingMatchId })
    }

    @Test
    fun `getUserFinishedMatches respects pagination`() = testWithHandleAndRollback { handle ->
        val repo = JDBIMatchRepository(handle)

        val userId = 1
        val otherPlayerId = 2
        val anotherPlayerId = 3

        // Setup: Create multiple finished matches for the user.
        repo.createFinishedMatch(userId, otherPlayerId)
        repo.createFinishedMatch(userId, anotherPlayerId)
        repo.createFinishedMatch(userId, anotherPlayerId)
        repo.createFinishedMatch(userId, anotherPlayerId)

        val matchesPage1 = repo.getUserFinishedMatches(0, 2, userId)
        val matchesPage2 = repo.getUserFinishedMatches(2, 2, userId)

        assertEquals(2, matchesPage1.size)
        assertEquals(2, matchesPage2.size)
        assertTrue(matchesPage1[0].id != matchesPage2[0].id)
        assertTrue(matchesPage1[1].id != matchesPage2[1].id)
    }
}

fun MatchRepository.createFinishedMatch(userId: Int, opponentId: Int): Int {
    // Create an ongoing match first
    val matchId = this.createMatch(2, userId, opponentId)

    // Set the match to finished
    this.setMatchState(matchId, MatchState.FINISHED)
    // Setting the outcome to BLACK_WON just for coherence
    this.setMatchOutcome(matchId, MatchOutcome.BLACK_WON)

    return matchId
}
