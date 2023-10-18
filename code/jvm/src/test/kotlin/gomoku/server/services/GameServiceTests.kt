package gomoku.server.services

import gomoku.server.failureOrNull
import gomoku.server.jbdiTest
import gomoku.server.repository.jdbi.JDBITransactionManager
import gomoku.server.services.errors.game.MakeMoveError
import gomoku.server.services.errors.game.MatchmakingError
import gomoku.server.services.game.GameService
import gomoku.server.testWithTransactionManagerAndRollback
import gomoku.utils.Failure
import gomoku.utils.Success
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.Test

class GameServiceTests {

    @Test
    fun `startMatchmakingProcess should start a new lobby if no existing lobbies with the same ruleId exist`() {
        val ruleId = 1
        val userId = 1 // Some random user

        testWithTransactionManagerAndRollback { transactionManager ->
            val gameService = GameService(transactionManager)

            val result = gameService.startMatchmakingProcess(ruleId, userId)

            assertTrue(result is Success && !result.value.isMatch)
        }
    }

    @Test
    fun `startMatchmakingProcess should return SamePlayer error if user tries to match with themselves`() {
        val ruleId = 1
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
        val ruleId = 1
        val userId1 = 1
        val userId2 = 2

        testWithTransactionManagerAndRollback { transactionManager ->
            val gameService = GameService(transactionManager)

            gameService.startMatchmakingProcess(ruleId, userId1)

            val result = gameService.startMatchmakingProcess(ruleId, userId2)

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
        val gameId = 2
        val userId = 2
        val position = 1

        testWithTransactionManagerAndRollback { transactionManager ->
            val gameService = GameService(transactionManager)

            val result = gameService.makeMove(gameId, userId, position)

            assertTrue(result is Failure)
            assertEquals(MakeMoveError.GameFinished, result.failureOrNull())
        }
    }

    @Test
    fun `makeMove should fail if trying to place a move on an already occupied position`() {
        val gameId = 3
        val userId = 3
        val position = 2

        testWithTransactionManagerAndRollback { transactionManager ->
            val gameService = GameService(transactionManager)

            // Simulate a move on the position
            gameService.makeMove(gameId, userId, position)

            val result = gameService.makeMove(gameId, userId, position)

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
        TODO()
    }

    @Test
    fun `makeMove should set the game state to finished if the move container is full`() {
        TODO()
    }

    // TODO: Test other game service methods
}
