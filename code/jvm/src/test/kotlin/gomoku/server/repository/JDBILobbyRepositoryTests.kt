package gomoku.server.repository

import gomoku.server.repository.lobby.JDBILobbyRepository
import gomoku.server.testWithHandleAndRollback
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class JDBILobbyRepositoryTests {

    @Test
    fun `getLobbyByRuleId should return correct lobby`() = testWithHandleAndRollback{ handle ->
        val repo = JDBILobbyRepository(handle)
        val ruleId = 1

        val lobby = repo.getLobbyByRuleId(ruleId)

        assertNotNull(lobby)
        assertEquals(1, lobby.rule.ruleId)
    }

    @Test
    fun `getLobbyByRuleId should return null for non-existing ruleId`() = testWithHandleAndRollback{ handle ->
        val repo = JDBILobbyRepository(handle)
        val ruleId = 2

        val lobby = repo.getLobbyByRuleId(ruleId)

        assertNull(lobby)
    }

    @Test
    fun `getLobbyByRuleId should be null for invalid ruleId`() = testWithHandleAndRollback{ handle ->
        val repo = JDBILobbyRepository(handle)
        val ruleId = -1

        val lobby = repo.getLobbyByRuleId(ruleId)

        assertNull(lobby)
    }

    @Test
    fun `getLobbies should return list of lobbies`() = testWithHandleAndRollback{ handle ->
        val repo = JDBILobbyRepository(handle)
        val lobbies = repo.getLobbies()

        assertTrue(lobbies.isNotEmpty())
        assertEquals(2, lobbies.size)

        val lobby1 = lobbies[0]
        assertEquals(1, lobby1.rule.ruleId)
        assertEquals(1, lobby1.userId)

        val lobby2 = lobbies[1]
        assertEquals(3, lobby2.rule.ruleId)
        assertEquals(2, lobby2.userId)
    }

    @Test
    fun `getLobbyByUserId should return correct lobby`() = testWithHandleAndRollback{ handle ->
        val repo = JDBILobbyRepository(handle)
        val userId = 1

        val lobby = repo.getLobbyByUserId(userId)

        assertNotNull(lobby)
        assertEquals(userId, lobby.userId)
    }

    @Test
    fun `getLobbyByUserId should return null for user not in any lobby`() = testWithHandleAndRollback{ handle ->
        val repo = JDBILobbyRepository(handle)
        val userId = 3

        val lobby = repo.getLobbyByUserId(userId)

        assertNull(lobby)
    }

    @Test
    fun `getLobbyByUserId should be null for invalid userId`() = testWithHandleAndRollback{ handle ->
        val repo = JDBILobbyRepository(handle)
        val userId = -1

        val lobby = repo.getLobbyByUserId(userId)

        assertNull(lobby)
    }

    @Test
    fun `createLobby should create a lobby for valid ruleId and userId`() = testWithHandleAndRollback{ handle ->
        val repo = JDBILobbyRepository(handle)
        val ruleId = 2
        val userId = 3

        val lobbyId = repo.createLobby(ruleId, userId)

        assertTrue(lobbyId > 0)

        val lobby = repo.getLobbyByRuleId(ruleId)
        assertNotNull(lobby)
        assertEquals(ruleId, lobby.rule.ruleId)
        assertEquals(userId, lobby.userId)
    }

    @Test
    fun `createLobby should throw exception for invalid ruleId`() = testWithHandleAndRollback{ handle ->
        val repo = JDBILobbyRepository(handle)
        val ruleId = -1
        val userId = 6

        assertThrows<Exception> {
            repo.createLobby(ruleId, userId)
        }
    }

    @Test
    fun `createLobby should throw exception for invalid userId`() = testWithHandleAndRollback{ handle ->
        val repo = JDBILobbyRepository(handle)
        val ruleId = 5
        val userId = -1

        assertThrows<Exception> {
            repo.createLobby(ruleId, userId)
        }
    }

    @Test
    fun `leaveLobby should remove user from existing lobby`() = testWithHandleAndRollback{ handle ->
        val repo = JDBILobbyRepository(handle)
        val userId = 1

        val result = repo.leaveLobby(userId)

        assertTrue(result)

        val lobby = repo.getLobbyByUserId(userId)
        assertNull(lobby)
    }

    @Test
    fun `leaveLobby should return false for user not in any lobby`() = testWithHandleAndRollback{ handle ->
        val repo = JDBILobbyRepository(handle)
        val userId = 3

        val result = repo.leaveLobby(userId)

        assertFalse(result)
    }

    @Test
    fun `leaveLobby should be false for invalid userId`() = testWithHandleAndRollback{ handle ->
        val repo = JDBILobbyRepository(handle)
        val userId = -1

        val result = repo.leaveLobby(userId)

        assertFalse(result)
    }
}