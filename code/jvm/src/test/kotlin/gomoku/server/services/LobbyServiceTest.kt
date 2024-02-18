package gomoku.server.services

import gomoku.server.TestClock
import gomoku.server.deleteLobbies
import gomoku.server.domain.user.Sha256TokenEncoder
import gomoku.server.domain.user.UsersDomain
import gomoku.server.domain.user.UsersDomainConfig
import gomoku.server.failureOrNull
import gomoku.server.services.errors.lobby.GetLobbyError
import gomoku.server.services.errors.lobby.JoinLobbyError
import gomoku.server.services.errors.lobby.LeaveLobbyError
import gomoku.server.services.game.GameService
import gomoku.server.services.lobby.LobbyService
import gomoku.server.services.user.UserService
import gomoku.server.testWithTransactionManagerAndRollback
import gomoku.utils.Failure
import gomoku.utils.Success
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.time.Duration

class LobbyServiceTest {

    private val usersDomain = UsersDomain(
        BCryptPasswordEncoder(),
        Sha256TokenEncoder(),
        UsersDomainConfig(10, Duration.INFINITE, Duration.INFINITE, 10)
    )
    private val clock = TestClock()

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
    fun `get all available lobbies from user`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            // before
            deleteLobbies()

            // test
            val lobbyService = LobbyService(transactionManager)
            val userService = UserService(transactionManager, usersDomain, clock)
            val newUser = userService.createUser(newTestUserName(), "!Kz9iYG$%TcB27f")
            require(newUser is Success)

            val nrOfLobbies = 3
            repeat(nrOfLobbies) {
                lobbyService.createLobby(it + 1, newUser.value.userId)
            }

            val sut = lobbyService.getLobbiesByUserId(newUser.value.userId)
            assertEquals(nrOfLobbies, sut.size)
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
    companion object {
        private fun newTestUserName() = "User${abs(Random.nextLong())}"
    }
}
