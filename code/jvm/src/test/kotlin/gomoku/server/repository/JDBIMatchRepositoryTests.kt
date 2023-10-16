package gomoku.server.repository

import gomoku.server.repository.game.JDBIMatchRepository
import gomoku.server.testWithHandleAndRollback
import org.junit.jupiter.api.Test
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
    }

    // TODO: ADD REMAINING REPOSITORY TESTS
}
