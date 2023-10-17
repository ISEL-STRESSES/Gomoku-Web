package gomoku.server.repository

import gomoku.server.domain.game.match.MatchOutcome
import gomoku.server.domain.game.match.MatchState
import gomoku.server.repository.match.JDBIMatchRepository
import gomoku.server.repository.user.JDBIUserRepository
import gomoku.server.testWithHandleAndRollback
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class JDBIMatchRepositoryTests {

    @Test
    fun `create rule and check if exists`() = testWithHandleAndRollback { handle ->

        val repo = JDBIMatchRepository(handle)

        val rule1 = repo.getRuleById(1)
        assertNotNull(rule1)

        val rule4 = repo.getRuleById(4)
        assertNull(rule4)

        val rules = repo.getAllRules()
        assertNotNull(rules)
        assertEquals(3, rules.size)
    }

    @Test
    fun `create match and check if exists`() = testWithHandleAndRollback { handle ->

        val repo = JDBIMatchRepository(handle)

        val matchIdNull = repo.getMatchById(11)
        assertNull(matchIdNull)

        val player1 = uRepo.getUserById(1)
        assertNotNull(player1)
        val player2 = uRepo.getUserById(2)
        assertNotNull(player2)

        val matchId = repo.createMatch(1, player1.uuid, player2.uuid)
        assertEquals(11, matchId)

        val matchState = repo.getMatchState(11)
        assertEquals(MatchState.ONGOING, matchState)

        val setMatchState = repo.setMatchState(11, MatchState.FINISHED)
        val newMatchState = repo.getMatchState(11)
        assertEquals(MatchState.FINISHED, newMatchState)

        val matchOutcome = repo.getMatchOutcome(11)
        assertNull(matchOutcome)
        val setMatchOutcome = repo.setMatchOutcome(11, MatchOutcome.BLACK_WON)
        val newMatchOutcome = repo.getMatchOutcome(11)
        assertNotNull(newMatchOutcome)
        assertEquals(MatchOutcome.BLACK_WON, newMatchOutcome)

        val matchRule = repo.getMatchRule(11)
        assertNotNull(matchRule)
        assertEquals(1, matchRule.ruleId)

        val matchPlayers = repo.getMatchPlayers(11)
        assertNotNull(matchPlayers)
        assertEquals(1, matchPlayers.first)
        assertEquals(2, matchPlayers.second)
    }

    // TODO: ADD REMAINING REPOSITORY TESTS
}
