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
    fun `getLobbiesByUserId should return list of lobbies`() = testWithHandleAndRollback { handle ->
        // before
        deleteLobbies(handle)

        // test
        val lobbyRepo = JDBILobbyRepository(handle)
        val userRepo = JDBIUserRepository(handle)

        val user1Username = "User" + Random.nextLong()
        val user1Password = "!Kz9iYG$%2TcB7f"

        val newUser1 = userRepo.storeUser(user1Username, PasswordValidationInfo(user1Password))

        assertTrue(lobbyRepo.getLobbiesByUserId(newUser1).isEmpty())

        val nrOfLobbies = 3
        repeat(nrOfLobbies) {
            lobbyRepo.createLobby(it + 1, newUser1)
        }

        val lobbies = lobbyRepo.getLobbiesByUserId(newUser1)
        assertTrue(lobbies.isNotEmpty())
        assertEquals(nrOfLobbies, lobbies.size)

        repeat(nrOfLobbies) {
            assertEquals(newUser1, lobbies[it].userId)
            assertEquals(it + 1, lobbies[it].rule.ruleId)
        }
    }

    @Test
    fun `getLobbiesByUserId should return correct lobby`() = testWithHandleAndRollback { handle ->
        val lobbyRepo = JDBILobbyRepository(handle)
        val userRepo = JDBIUserRepository(handle)
        val newUsername = "User" + Random.nextLong()
        val newPassword = "!Kz9iYG$%2TcB7f"
        val newUser = userRepo.storeUser(newUsername, PasswordValidationInfo(newPassword))
        val lobbyId = lobbyRepo.createLobby(1, newUser)
        lobbyRepo.createLobby(2, newUser)
        val lobbies = lobbyRepo.getLobbiesByUserId(newUser)

        assertTrue(lobbies.isNotEmpty())
        assertTrue(lobbies.size == 2)
        assertEquals(newUser, lobbies.first().userId)
        assertEquals(1, lobbies.first().rule.ruleId)
        assertEquals(lobbyId, lobbies.first().id)
    }

    @Test
    fun `getLobbiesByUserId should return empty list for user not in any lobby`() = testWithHandleAndRollback { handle ->
        val repo = JDBILobbyRepository(handle)
        val userId = 3

        val lobby = repo.getLobbiesByUserId(userId)

        assertTrue(lobby.isEmpty())
    }

    @Test
    fun `getLobbiesByUserId should be empty for invalid userId`() = testWithHandleAndRollback { handle ->
        val repo = JDBILobbyRepository(handle)
        val userId = -1

        val lobby = repo.getLobbiesByUserId(userId)

        assertTrue(lobby.isEmpty())
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

        assertTrue(repo.getLobbiesByUserId(newUser).isEmpty())
        repo.createLobby(1, newUser)

        val lobbyId = repo.getLobbiesByUserId(newUser).first().id
        assertNotNull(lobbyId)
        assertTrue(repo.getLobbiesByUserId(newUser).isNotEmpty())

        val result = repo.leaveLobby(lobbyId, newUser)
        assertTrue(result)

        val lobby = repo.getLobbiesByUserId(newUser)
        assertTrue(lobby.isEmpty())
    }

    @Test
    fun `leaveLobby should return false for user not in any lobby`() = testWithHandleAndRollback { handle ->
        val repo = JDBILobbyRepository(handle)
        val userId = 3
        val randomLobbyId = Random.nextInt()
        val result = repo.leaveLobby(randomLobbyId, userId)

        assertFalse(result)
    }

    @Test
    fun `leaveLobby should be false for invalid userId`() = testWithHandleAndRollback { handle ->
        val repo = JDBILobbyRepository(handle)
        val userId = -1
        val randomLobbyId = Random.nextInt()
        val result = repo.leaveLobby(randomLobbyId, userId)

        assertFalse(result)
    }
}
