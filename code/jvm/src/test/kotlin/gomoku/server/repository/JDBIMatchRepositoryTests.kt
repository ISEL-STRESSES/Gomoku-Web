package gomoku.server.repository

import gomoku.server.domain.game.rules.BoardSize
import gomoku.server.domain.game.rules.ProOpeningRules
import gomoku.server.repository.game.JDBIMatchRepository
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

        val ruleProX19 = repo.getRuleById(4)
        assertNull(ruleProX19)

        val newRuleProX19 = repo.getRuleId(ProOpeningRules(BoardSize.X19))
        assertNotNull(newRuleProX19)
    }

    @Test
    fun `create lobby and checks if exists`() = testWithHandleAndRollback { handle ->

        val repo = JDBIMatchRepository(handle)

        val lobbyNull = repo.getLobbyById(1)
        assertNull(lobbyNull)

        val newLobby = repo.joinLobby(1, 1)
        assertNotNull(newLobby)
        assertEquals(newLobby, 1)
    }
}
