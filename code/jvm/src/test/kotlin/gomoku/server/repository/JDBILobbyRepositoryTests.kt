package gomoku.server.repository

import gomoku.server.deleteLobbies
import gomoku.server.domain.user.PasswordValidationInfo
import gomoku.server.repository.lobby.JDBILobbyRepository
import gomoku.server.repository.user.JDBIUserRepository
import gomoku.server.testWithHandleAndRollback
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class JDBILobbyRepositoryTests {

    @Test
    fun `getLobbyByRuleId should return correct lobby`() = testWithHandleAndRollback { handle ->
        val repo = JDBILobbyRepository(handle)
        val ruleId = 1
        val userId = 1

        val lobbyId = repo.createLobby(ruleId, userId)
        val lobby = repo.getLobbyByRuleId(ruleId)

        assertNotNull(lobby)
        assertEquals(ruleId, lobby.rule.ruleId)
        assertEquals(userId, lobby.userId)
        assertEquals(lobbyId, lobby.id)
    }

    @Test
    fun `getLobbyByRuleId should return null for non-existing ruleId`() = testWithHandleAndRollback { handle ->
        // before
        deleteLobbies(handle)
        // test
        val repo = JDBILobbyRepository(handle)
        val ruleId = 2

        val lobby = repo.getLobbyByRuleId(ruleId)

        assertNull(lobby)
    }

    @Test
    fun `getLobbyByRuleId should be null for invalid ruleId`() = testWithHandleAndRollback { handle ->
        val repo = JDBILobbyRepository(handle)
        val ruleId = -1

        val lobby = repo.getLobbyByRuleId(ruleId)

        assertNull(lobby)
    }

    @Test
    fun `getLobbies should return list of lobbies`() = testWithHandleAndRollback { handle ->
        // before
        deleteLobbies(handle)
        // test
        val lobbyRepo = JDBILobbyRepository(handle)
        val userRepo = JDBIUserRepository(handle)

        val user1Username = "User" + Random.nextLong()
        val user1Password = "!Kz9iYG$%2TcB7f"
        val user2Username = "User" + Random.nextLong()
        val user2Password = "!Kz9iYG$%2TcB7f"

        val newUser1 = userRepo.storeUser(user1Username, PasswordValidationInfo(user1Password))
        val newUser2 = userRepo.storeUser(user2Username, PasswordValidationInfo(user2Password))

        assertTrue(lobbyRepo.getLobbies().isEmpty())

        lobbyRepo.createLobby(1, newUser1)

        val lobbies1 = lobbyRepo.getLobbies()

        assertTrue(lobbies1.isNotEmpty())
        assertEquals(1, lobbies1.size)

        lobbyRepo.createLobby(2, newUser2)

        val lobbies2 = lobbyRepo.getLobbies()

        assertTrue(lobbies2.isNotEmpty())
        assertEquals(2, lobbies2.size)

        val lobby1 = lobbies2[0]
        assertEquals(1, lobby1.rule.ruleId)
        assertEquals(newUser1, lobby1.userId)

        val lobby2 = lobbies2[1]
        assertEquals(2, lobby2.rule.ruleId)
        assertEquals(newUser2, lobby2.userId)
    }

    @Test
    fun `getLobbyByUserId should return correct lobby`() = testWithHandleAndRollback { handle ->
        val lobbyRepo = JDBILobbyRepository(handle)
        val userRepo = JDBIUserRepository(handle)
        val newUsername = "User" + Random.nextLong()
        val newPassword = "!Kz9iYG$%2TcB7f"
        val newUser = userRepo.storeUser(newUsername, PasswordValidationInfo(newPassword))
        val lobbyId = lobbyRepo.createLobby(1, newUser)
        val lobby = lobbyRepo.getLobbyByUserId(newUser)

        assertNotNull(lobby)
        assertEquals(newUser, lobby.userId)
        assertEquals(1, lobby.rule.ruleId)
        assertEquals(lobbyId, lobby.id)
    }

    @Test
    fun `getLobbyByUserId should return null for user not in any lobby`() = testWithHandleAndRollback { handle ->
        val repo = JDBILobbyRepository(handle)
        val userId = 3

        val lobby = repo.getLobbyByUserId(userId)

        assertNull(lobby)
    }

    @Test
    fun `getLobbyByUserId should be null for invalid userId`() = testWithHandleAndRollback { handle ->
        val repo = JDBILobbyRepository(handle)
        val userId = -1

        val lobby = repo.getLobbyByUserId(userId)

        assertNull(lobby)
    }

    @Test
    fun `createLobby should create a lobby for valid ruleId and userId`() = testWithHandleAndRollback { handle ->
        // before
        deleteLobbies(handle)
        // test
        val repo = JDBILobbyRepository(handle)
        val ruleId = 3
        val userId = 3

        val lobbyId = repo.createLobby(ruleId, userId)

        assertTrue(lobbyId > 0)

        val lobby = repo.getLobbyByRuleId(ruleId)
        assertNotNull(lobby)
        assertEquals(ruleId, lobby.rule.ruleId)
        assertEquals(userId, lobby.userId)
    }

    @Test
    fun `createLobby should throw exception for invalid ruleId`() = testWithHandleAndRollback { handle ->
        val repo = JDBILobbyRepository(handle)
        val ruleId = -1
        val userId = 6

        assertThrows<Exception> {
            repo.createLobby(ruleId, userId)
        }
    }

    @Test
    fun `createLobby should throw exception for invalid userId`() = testWithHandleAndRollback { handle ->
        val repo = JDBILobbyRepository(handle)
        val ruleId = 5
        val userId = -1

        assertThrows<Exception> {
            repo.createLobby(ruleId, userId)
        }
    }

    @Test
    fun `leaveLobby should remove user from existing lobby`() = testWithHandleAndRollback { handle ->
        // before
        deleteLobbies(handle)
        // test
        val repo = JDBILobbyRepository(handle)
        val userRepo = JDBIUserRepository(handle)
        val newUsername = "User" + Random.nextLong()
        val newPassword = "!Kz9iYG$%2TcB7f"
        val newUser = userRepo.storeUser(newUsername, PasswordValidationInfo(newPassword))

        assertTrue(repo.getLobbies().isEmpty())
        repo.createLobby(1, newUser)
        assertTrue(repo.getLobbies().isNotEmpty())
        val result = repo.leaveLobby(newUser)
        assertTrue(result)
        val lobby = repo.getLobbyByUserId(newUser)
        assertNull(lobby)
        assertTrue(repo.getLobbies().isEmpty())
    }

    @Test
    fun `leaveLobby should return false for user not in any lobby`() = testWithHandleAndRollback { handle ->
        val repo = JDBILobbyRepository(handle)
        val userId = 3

        val result = repo.leaveLobby(userId)

        assertFalse(result)
    }

    @Test
    fun `leaveLobby should be false for invalid userId`() = testWithHandleAndRollback { handle ->
        val repo = JDBILobbyRepository(handle)
        val userId = -1

        val result = repo.leaveLobby(userId)

        assertFalse(result)
    }
}
