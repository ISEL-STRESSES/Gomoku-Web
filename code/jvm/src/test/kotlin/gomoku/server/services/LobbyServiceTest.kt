package gomoku.server.services

import gomoku.server.deleteLobbies
import gomoku.server.failureOrNull
import gomoku.server.services.errors.lobby.GetLobbyError
import gomoku.server.services.errors.lobby.JoinLobbyError
import gomoku.server.services.errors.lobby.LeaveLobbyError
import gomoku.server.services.game.GameService
import gomoku.server.services.lobby.LobbyService
import gomoku.server.testWithTransactionManagerAndRollback
import gomoku.utils.Failure
import gomoku.utils.Success
import org.junit.jupiter.api.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class LobbyServiceTest {

    @Test
    fun `leave lobby`() {
        val userTestId = 1
        val ruleTestId = 1
        testWithTransactionManagerAndRollback { transactionManager ->
            // before
            deleteLobbies(transactionManager)

            // test
            val lobbyService = LobbyService(transactionManager)
            val lobby = lobbyService.createLobby(ruleTestId, userTestId)
            val sut = lobbyService.leaveLobby(lobby.id, userTestId)
            assert(sut is Success)
        }
    }

    @Test
    fun `leave lobby from un-existing lobby`() {
        val userTestId = 1
        val ruleTestId = 1
        testWithTransactionManagerAndRollback { transactionManager ->
            // before
            deleteLobbies()

            // test
            val lobbyService = LobbyService(transactionManager)

            lobbyService.createLobby(ruleTestId, userTestId)

            val sut = lobbyService.leaveLobby(Int.MAX_VALUE, userTestId)
            assert(sut is Failure)
            assert(sut.failureOrNull() is LeaveLobbyError.LobbyNotFound)
        }
    }

    @Test // TODO("How can i do this if its required for a user to be in a lobby, how can i differentiate from different errors")
    fun `leave lobby with an un-existing user id`() {
        val userTestId = Int.MAX_VALUE
        testWithTransactionManagerAndRollback { transactionManager ->
            // before
            deleteLobbies()

            // test
            val lobbyService = LobbyService(transactionManager)
        }
    }

    @Test
    fun `create Lobby`() {
        val testRuleId = 1
        val testUserId = 1
        testWithTransactionManagerAndRollback { transactionManager ->
            // before
            deleteLobbies()
            // test
            val lobbyService = LobbyService(transactionManager)

            val newLobby = lobbyService.createLobby(testRuleId, testUserId)
            assertFalse(newLobby.isGame)

            val sut = lobbyService.getLobbyById(newLobby.id)
            require(sut is Success)

            assertEquals(sut.value.userId, testUserId)
            assertEquals(sut.value.rule.ruleId, testRuleId)
        }
    }

    @Test
    fun `join lobby`() {
        val userTest1Id = 1
        val userTest2Id = 2
        val ruleTestId = 1
        testWithTransactionManagerAndRollback { transactionManager ->
            // before
            deleteLobbies()

            // test
            val lobbyService = LobbyService(transactionManager)

            val lobby = lobbyService.createLobby(ruleTestId, userTest1Id)
            val sut = lobbyService.joinLobby(lobby.id, userTest2Id)
            require(sut is Success)
            assert(sut.value.isGame)
        }
    }

    @Test
    fun `join lobby with and un-existing lobby`() {
        val userTestId = 1
        testWithTransactionManagerAndRollback { transactionManager ->
            // before
            deleteLobbies()

            // test
            val lobbyService = LobbyService(transactionManager)

            val sut = lobbyService.joinLobby(Int.MAX_VALUE, userTestId)
            require(sut is Failure)

            assert(sut.value is JoinLobbyError.LobbyNotFound)
        }
    }

    @Test
    fun `join lobby twice with same user`() {
        val userTestId = 1
        val ruleTestId = 1
        testWithTransactionManagerAndRollback { transactionManager ->
            // before
            deleteLobbies()

            // test
            val lobbyService = LobbyService(transactionManager)
            val lobby = lobbyService.createLobby(ruleTestId, userTestId)
            val sut = lobbyService.joinLobby(lobby.id, userTestId)
            require(sut is Failure)

            assert(sut.value is JoinLobbyError.UserAlreadyInLobby)
        }
    }

    @Test
    fun `join lobby turns into game`() {
        val userTest1Id = 1
        val userTest2Id = 2
        val ruleTestId = 1
        testWithTransactionManagerAndRollback { transactionManager ->
            // before
            deleteLobbies()

            // test
            val lobbyService = LobbyService(transactionManager)
            val gameService = GameService(transactionManager)

            val lobby = lobbyService.createLobby(ruleTestId, userTest1Id)
            val sut = lobbyService.joinLobby(lobby.id, userTest2Id)
            require(sut is Success)
            assert(sut.value.isGame)

            val sutGameDetails = gameService.getGame(sut.value.id, userTest1Id)
            require(sutGameDetails is Success)
            assertEquals(sutGameDetails.value.rules.ruleId, ruleTestId)
            assertContains(arrayOf(userTest1Id, userTest2Id), sutGameDetails.value.playerBlack)
            assertContains(arrayOf(userTest1Id, userTest2Id), sutGameDetails.value.playerWhite)
            assert(sutGameDetails.value.moveContainer.isEmpty())
        }
    }

    @Test
    fun `get all available lobbies`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            // before
            deleteLobbies()

            // test
            val lobbyService = LobbyService(transactionManager)

            val nrOfLobbies = 3
            repeat(nrOfLobbies) {
                lobbyService.createLobby(it + 1, it + 1)
            }

            val sut = lobbyService.getLobbies()
            assertEquals(sut.size, nrOfLobbies)
        }
    }

    @Test
    fun `get lobby by id`() {
        val userTestId = 1
        val ruleTestId = 1
        testWithTransactionManagerAndRollback { transactionManager ->
            // before
            deleteLobbies()

            // test
            val lobbyService = LobbyService(transactionManager)

            val lobby = lobbyService.createLobby(ruleTestId, userTestId)
            val sut = lobbyService.getLobbyById(lobby.id)
            assert(sut is Success)
            require(sut is Success)
            assertEquals(sut.value.userId, userTestId)
            assertEquals(sut.value.rule.ruleId, ruleTestId)
        }
    }

    @Test
    fun `get lobby with un-existing id`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            // before
            deleteLobbies()

            // test
            val lobbyService = LobbyService(transactionManager)

            val sut = lobbyService.getLobbyById(Int.MAX_VALUE)
            assert(sut is Failure)
            require(sut is Failure)
            assert(sut.value is GetLobbyError.LobbyNotFound)
        }
    }
}
