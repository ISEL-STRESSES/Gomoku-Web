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

        val rule4 = repo.getRuleById(4)
        assertNull(rule4)

        val ruleProX15 = ProOpeningRules(BoardSize.X15)
        val ruleProX15Id = repo.getRuleId(ruleProX15)
        assertNotNull(ruleProX15Id)
    }

    @Test
    fun `create lobby and checks if exists`() = testWithHandleAndRollback { handle ->

        val repo = JDBIMatchRepository(handle)


    }
}
