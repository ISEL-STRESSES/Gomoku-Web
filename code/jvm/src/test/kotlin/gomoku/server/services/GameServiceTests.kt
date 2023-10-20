package gomoku.server.services

import gomoku.server.TestClock
import gomoku.server.domain.game.match.FinishedMatch
import gomoku.server.domain.user.Sha256TokenEncoder
import gomoku.server.domain.user.UsersDomain
import gomoku.server.domain.user.UsersDomainConfig
import gomoku.server.failureOrNull
import gomoku.server.repository.createFinishedMatch
import gomoku.server.services.errors.game.MakeMoveError
import gomoku.server.services.errors.game.MatchmakingError
import gomoku.server.services.game.GameService
import gomoku.server.services.user.UserService
import gomoku.server.testWithTransactionManagerAndRollback
import gomoku.utils.Failure
import gomoku.utils.Success
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.time.Duration

class GameServiceTests {

    private val usersDomain = UsersDomain(
        BCryptPasswordEncoder(),
        Sha256TokenEncoder(),
        UsersDomainConfig(10, Duration.INFINITE, Duration.INFINITE, 10)
    )
    private val clock = TestClock()

    @Test
    fun `startMatchmakingProcess should start a new lobby if no existing lobbies with the same ruleId exist`() {
        val ruleId = 2
        val userId = 1 // Some random user

        testWithTransactionManagerAndRollback { transactionManager ->
            val gameService = GameService(transactionManager)

            val result = gameService.startMatchmakingProcess(ruleId, userId)

            assertTrue(result is Success && !result.value.isMatch)
        }
    }

    @Test
    fun `startMatchmakingProcess should return SamePlayer error if user tries to match with themselves`() {
        val ruleId = 2
        val userId = 3

        testWithTransactionManagerAndRollback { transactionManager ->
            val gameService = GameService(transactionManager)

            // Simulate the presence of an existing lobby
            gameService.startMatchmakingProcess(ruleId, userId)

            val result = gameService.startMatchmakingProcess(ruleId, userId)

            assertTrue(result is Failure)
            assertEquals(MatchmakingError.SamePlayer, result.failureOrNull())
        }
    }

    @Test
    fun `startMatchmakingProcess should handle the case where the user is matched into a game`() {
        val ruleId = 2

        testWithTransactionManagerAndRollback { transactionManager ->
            // before
            val userService = UserService(transactionManager = transactionManager, clock = clock, usersDomain = usersDomain)
            val randomPassword = "ByQYP78&j7Aug2" // secure password
            val user1Id = userService.createUser("test1", randomPassword)
            val user2Id = userService.createUser("test2", randomPassword)

            require(user1Id is Success)
            require(user2Id is Success)
            // sut
            val gameService = GameService(transactionManager)

            val result1 = gameService.startMatchmakingProcess(ruleId, user1Id.value)

            assertFalse(result1 is Success && result1.value.isMatch)

            val result = gameService.startMatchmakingProcess(ruleId, user2Id.value)

            assertTrue(result is Success && result.value.isMatch)
        }
    }

    @Test
    fun `makeMove should successfully make a move for an ongoing game`() {
        val gameId = 1
        val userId = 1
        val position = 0

        testWithTransactionManagerAndRollback { transactionManager ->
            val gameService = GameService(transactionManager)

            val result = gameService.makeMove(gameId, userId, position)

            assertTrue(result is Success)
        }
    }

    @Test
    fun `makeMove should fail to make a move for a finished game`() {
        val gameId = 7
        val userId = 7
        val position = 1

        testWithTransactionManagerAndRollback { transactionManager ->
            val gameService = GameService(transactionManager)
            val game = gameService.getGame(gameId)
            requireNotNull(game)

            val finishedGameId = transactionManager.run {
                it.matchRepository.createFinishedMatch(game.playerBlack, game.playerWhite)
            }

            val result = gameService.makeMove(finishedGameId, userId, position)

            require(result is Failure)
            assertEquals(MakeMoveError.GameFinished, result.failureOrNull())
        }
    }

    @Test
    fun `makeMove should fail if trying to place a move on an already occupied position`() {
        val gameId = 3
        val userId1 = 3
        val userId2 = 6
        val position = 2

        testWithTransactionManagerAndRollback { transactionManager ->
            val gameService = GameService(transactionManager)

            // Simulate a move on the position
            gameService.makeMove(gameId, userId1, position)

            val result = gameService.makeMove(gameId, userId2, position)

            assertTrue(result is Failure)
            assertEquals(MakeMoveError.AlreadyOccupied, result.failureOrNull())
        }
    }

    @Test
    fun `makeMove should fail if trying to move out of turn`() {
        val gameId = 4
        val userId1 = 4
        val position1 = 3
        val position2 = 4

        testWithTransactionManagerAndRollback { transactionManager ->
            val gameService = GameService(transactionManager)

            gameService.makeMove(gameId, userId1, position1)

            val result = gameService.makeMove(gameId, userId1, position2)

            assertTrue(result is Failure)
            assertEquals(MakeMoveError.InvalidTurn, result.failureOrNull())
        }
    }

    @Test
    fun `makeMove should resolve to a winning move if the move results in a win`() {
        val gameId = 10
        val userId1 = 10
        val position = 13

        testWithTransactionManagerAndRollback { transactionManager ->
            val gameService = GameService(transactionManager)

            val result = gameService.makeMove(gameId, userId1, position)

            assertTrue(result is Success)
        }
    }

    @Test
    fun `makeMove should set the game state to finished if the move container is full`() {
        val gameId = 11
        val userId1 = 1
        val position = 4

        testWithTransactionManagerAndRollback { transactionManager ->
            val gameService = GameService(transactionManager)

            val result = gameService.makeMove(gameId, userId1, position)

            assertTrue(result is Success && result.value is FinishedMatch && (result.value as FinishedMatch).getWinnerIdOrNull() == userId1)
        }
    }

    @Test
    fun `leaveLobby should be true if the user was on it`() {
        val ruleId = 2
        val userId = 3

        testWithTransactionManagerAndRollback { transactionManager ->
            val gameService = GameService(transactionManager)

            gameService.startMatchmakingProcess(ruleId, userId)

            val result = gameService.leaveLobby(userId)

            assertTrue(result)
        }
    }

    @Test
    fun `leaveLobby should be false if the user was not on it`() {
        val userId = 16

        testWithTransactionManagerAndRollback { transactionManager ->
            val gameService = GameService(transactionManager)

            val result = gameService.leaveLobby(userId)

            assertTrue(!result)
        }
    }

    @Test
    fun `getAvailableRules `() {
        testWithTransactionManagerAndRollback { transactionManager ->
            val gameService = GameService(transactionManager)

            val result = gameService.getAvailableRules()

            assertTrue(result.isNotEmpty())
            assertEquals(3, result.size)

            println(result)
        }
    }

    @Test
    fun `getCurrentTurnPlayerId should return the id of the player whose turn it is`() {
        val gameId = 1

        testWithTransactionManagerAndRollback { transactionManager ->
            val gameService = GameService(transactionManager)

            val result = gameService.getCurrentTurnPlayerId(gameId)

            assertEquals(1, result)
        }
    }

    @Test
    fun `getCurrentTurnPlayerId should return null if the game doesn't exist`() {
        val gameId = 100

        testWithTransactionManagerAndRollback { transactionManager ->
            val gameService = GameService(transactionManager)

            val result = gameService.getCurrentTurnPlayerId(gameId)

            assertNull(result)
        }
    }

    @Test
    fun `getCurrentTurnPlayerId should return null if the game is finished`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            val gameService = GameService(transactionManager)
            // before
            // forcing a finished match
            val finishedMatchId = transactionManager.run {
                it.matchRepository.createFinishedMatch(1, 2)
            }
            // sut
            val result = gameService.getCurrentTurnPlayerId(finishedMatchId)

            assertNull(result)
        }
    }

    @Test
    fun `getGame should return a Match if the game exists`() {
        val gameId = 1

        testWithTransactionManagerAndRollback { transactionManager ->
            val gameService = GameService(transactionManager)

            val result = gameService.getGame(gameId)

            assertNotNull(result)
            assertEquals(result.matchId, gameId)
            assertEquals(result.playerBlack, 1)
            assertEquals(result.playerWhite, 2)
            assertEquals(result.rules.ruleId, 1)
        }
    }

    @Test
    fun `getGame should return null if the game doesn't exist`() {
        val gameId = 100

        testWithTransactionManagerAndRollback { transactionManager ->
            val gameService = GameService(transactionManager)

            val result = gameService.getGame(gameId)

            assertNull(result)
        }
    }

    @Test
    fun `getUserFinishedMatches should return a list of finished matches`() {
        val userId = 7

        testWithTransactionManagerAndRollback { transactionManager ->
            val gameService = GameService(transactionManager)
            // before
            // forcing a finished match
            transactionManager.run {
                it.matchRepository.createFinishedMatch(7, 8)
            }
            // sut
            val result = gameService.getUserFinishedMatches(0, 10, userId)

            assertTrue(result.isNotEmpty())
        }
    }

    @Test
    fun `getUserFinishedMatches should return an empty list if the user has no finished matches`() {
        val userId = 2

        testWithTransactionManagerAndRollback { transactionManager ->
            val gameService = GameService(transactionManager)

            val result = gameService.getUserFinishedMatches(0, 10, userId)

            assertTrue(result.isEmpty())
        }
    }

    @Test
    fun `getUserFinishedMatches should return an empty list if the user doesn't exist`() {
        val userId = 100

        testWithTransactionManagerAndRollback { transactionManager ->
            val gameService = GameService(transactionManager)

            val result = gameService.getUserFinishedMatches(0, 10, userId)

            assertTrue(result.isEmpty())
        }
    }
}
