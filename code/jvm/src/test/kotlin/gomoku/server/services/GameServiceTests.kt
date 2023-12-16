package gomoku.server.services

import gomoku.server.deleteLobbies
import gomoku.server.domain.game.game.FinishedGame
import gomoku.server.failureOrNull
import gomoku.server.repository.createFinishedGame
import gomoku.server.services.errors.game.GetGameError
import gomoku.server.services.errors.game.MakeMoveError
import gomoku.server.services.errors.lobby.LeaveLobbyError
import gomoku.server.services.game.GameService
import gomoku.server.services.lobby.LobbyService
import gomoku.server.successOrNull
import gomoku.server.testWithTransactionManagerAndRollback
import gomoku.utils.Failure
import gomoku.utils.Success
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.Test

class GameServiceTests {

//    @Test
//    fun `startMatchmakingProcess should start a new lobby if no existing lobbies with the same ruleId exist`() {
//        val ruleId = 2
//        val userId = 1 // Some random user
//
//        testWithTransactionManagerAndRollback { transactionManager ->
//            // before
//            deleteLobbies(transactionManager)
//            // test
//            val gameService = GameService(transactionManager)
//
//            val result = gameService.startMatchmakingProcess(ruleId, userId)
//
//            assertTrue(result is Success && !result.value.isGame)
//        }
//    }
//
//    @Test
//    fun `startMatchmakingProcess should return SamePlayer error if user tries to match with themselves`() {
//        val ruleId = 2
//        val userId = 3
//
//        testWithTransactionManagerAndRollback { transactionManager ->
//            // before
//            deleteLobbies(transactionManager)
//            // test
//            val gameService = GameService(transactionManager)
//
//            // Simulate the presence of an existing lobby
//            gameService.startMatchmakingProcess(ruleId, userId)
//
//            val result = gameService.startMatchmakingProcess(ruleId, userId)
//
//            assertTrue(result is Failure)
//            assertEquals(MatchmakingError.SamePlayer, result.failureOrNull())
//        }
//    }
//
//    @Test
//    fun `startMatchmakingProcess should handle the case where the user is matched into a game`() {
//        val ruleId = 2
//
//        testWithTransactionManagerAndRollback { transactionManager ->
//            // before
//            deleteLobbies(transactionManager)
//            // user before
//            val userService =
//                UserService(transactionManager = transactionManager, clock = clock, usersDomain = usersDomain)
//            val randomPassword = "ByQYP78&j7Aug2" // secure password
//            val user1Id = userService.createUser("test1", randomPassword)
//            val user2Id = userService.createUser("test2", randomPassword)
//
//            require(user1Id is Success)
//            require(user2Id is Success)
//            // sut
//            val gameService = GameService(transactionManager)
//
//            val result1 = gameService.startMatchmakingProcess(ruleId, user1Id.value.userId)
//
//            assertFalse(result1 is Success && result1.value.isGame)
//
//            val result = gameService.startMatchmakingProcess(ruleId, user2Id.value.userId)
//
//            assertTrue(result is Success && result.value.isGame)
//        }
//    }

    @Test
    fun `makeMove should successfully make a move for an ongoing game`() {
        val gameId = 1
        val userId = 1
        val positionX = 0
        val positionY = 0

        testWithTransactionManagerAndRollback { transactionManager ->
            val gameService = GameService(transactionManager)

            val result = gameService.makeMove(gameId, userId, positionX, positionY)

            assertTrue(result is Success)
        }
    }

    @Test
    fun `makeMove should fail to make a move for a finished game`() {
        val gameId = 7
        val userId = 7
        val positionX = 1
        val positionY = 0

        testWithTransactionManagerAndRollback { transactionManager ->
            val gameService = GameService(transactionManager)
            val game = gameService.getGame(gameId, userId).successOrNull()!!

            val finishedGameId = transactionManager.run {
                it.gameRepository.createFinishedGame(game.playerBlack, game.playerWhite)
            }

            val result = gameService.makeMove(finishedGameId, userId, positionX, positionY)

            require(result is Failure)
            assertEquals(MakeMoveError.GameFinished, result.failureOrNull())
        }
    }

    @Test
    fun `makeMove should fail if trying to place a move on an already occupied position`() {
        val gameId = 3
        val userId1 = 3
        val userId2 = 6
        val positionX = 2
        val positionY = 0

        testWithTransactionManagerAndRollback { transactionManager ->
            val gameService = GameService(transactionManager)

            // Simulate a move on the position
            gameService.makeMove(gameId, userId1, positionX, positionY)

            val result = gameService.makeMove(gameId, userId2, positionX, positionY)

            assertTrue(result is Failure)
            assertEquals(MakeMoveError.AlreadyOccupied, result.failureOrNull())
        }
    }

    @Test
    fun `makeMove should fail if trying to move out of turn`() {
        val gameId = 4
        val userId1 = 4
        val position1X = 3
        val position1Y = 0
        val position2X = 4
        val position2Y = 0

        testWithTransactionManagerAndRollback { transactionManager ->
            val gameService = GameService(transactionManager)

            gameService.makeMove(gameId, userId1, position1X, position1Y)

            val result = gameService.makeMove(gameId, userId1, position2X, position2Y)

            assertTrue(result is Failure)
            assertEquals(MakeMoveError.InvalidTurn, result.failureOrNull())
        }
    }

    @Test
    fun `makeMove should resolve to a winning move if the move results in a win`() {
        val gameId = 10
        val userId1 = 10
        val positionX = 13
        val positionY = 13

        testWithTransactionManagerAndRollback { transactionManager ->
            val gameService = GameService(transactionManager)

            val result = gameService.makeMove(gameId, userId1, positionX, positionY)

            assertTrue(result is Success)
        }
    }

    @Test
    fun `makeMove should set the game state to finished if the move container is full`() {
        val gameId = 11
        val userId1 = 21
        val positionX = 4
        val positionY = 0

        testWithTransactionManagerAndRollback { transactionManager ->
            // test
            val gameService = GameService(transactionManager)

            val result = gameService.makeMove(gameId, userId1, positionX, positionY)
            assertTrue(result is Success)
            require(result is Success)
            assertTrue(result.value is FinishedGame)
            require(result.value is FinishedGame)
            assertTrue((result.value as FinishedGame).getWinnerIdOrNull() == userId1)
        }
    }

//    @Test
//    fun `leaveLobby should be true if the user was on it`() {
//        val ruleId = 2
//        val userId = 3
//
//        testWithTransactionManagerAndRollback { transactionManager ->
//            // before
//            deleteLobbies(transactionManager)
//            // test
//            val gameService = GameService(transactionManager)
//            val lobbyService = LobbyService(transactionManager)
//
//            val res = gameService.startMatchmakingProcess(ruleId, userId).successOrNull()!!
//
//            val result = lobbyService.leaveLobby(res.id, userId)
//
//            assertTrue(result is Success)
//        }
//    }

    @Test
    fun `leaveLobby should be LobbyNotFound if the lobby doesn't exist`() {
        val userId = 2

        testWithTransactionManagerAndRollback { transactionManager ->
            val lobbyService = LobbyService(transactionManager)

            val result = lobbyService.leaveLobby(2, userId)

            assertTrue(result is Failure)
            assertTrue(result.failureOrNull() == LeaveLobbyError.LobbyNotFound)
        }
    }

    @Test
    fun `leaveLobby should be UserNotInLobby if the user is not in the lobby`() {
        val userId = 2

        testWithTransactionManagerAndRollback { transactionManager ->
            val lobbyService = LobbyService(transactionManager)
            val dummyLobby = transactionManager.run {
                deleteLobbies(transactionManager)
                it.lobbyRepository.createLobby(1, 4)
            }

            val result = lobbyService.leaveLobby(dummyLobby, userId)

            assertTrue(result is Failure)

            assertTrue(result.failureOrNull() == LeaveLobbyError.UserNotInLobby)
        }
    }

    @Test
    fun `getAvailableRules `() {
        testWithTransactionManagerAndRollback { transactionManager ->
            val gameService = GameService(transactionManager)

            val result = gameService.getAvailableRules()

            assertTrue(result.isNotEmpty())
            assertEquals(3, result.size)
        }
    }

    @Test
    fun `getCurrentTurnPlayerId should return the id of the player whose turn it is with logged in player`() {
        val gameId = 1

        testWithTransactionManagerAndRollback { transactionManager ->
            // before
            deleteLobbies(transactionManager)
            // test
            val gameService = GameService(transactionManager)

            val result = gameService.getCurrentTurnPlayerId(gameId, 1) /* take away magic number */

            assertTrue(result is Success)
            require(result is Success)
            assertEquals(1, result.value.turn)
        }
    }

    @Test
    fun `getCurrentTurnPlayerId should return null if the game doesn't exist`() {
        val gameId = 100

        testWithTransactionManagerAndRollback { transactionManager ->
            // before
            deleteLobbies(transactionManager)
            // test
            val gameService = GameService(transactionManager)

            val result = gameService.getCurrentTurnPlayerId(gameId, 1) /* take away magic number */

            assertTrue(result is Failure)
        }
    }

    @Test
    fun `getCurrentTurnPlayerId should return null if the game is finished`() {
        testWithTransactionManagerAndRollback { transactionManager ->
            // before
            deleteLobbies(transactionManager)
            // test before
            // forcing a finished game
            val gameService = GameService(transactionManager)
            val finishedGameId = transactionManager.run {
                it.gameRepository.createFinishedGame(1, 2)
            }
            // sut
            val result = gameService.getCurrentTurnPlayerId(finishedGameId, 1) /* take away magic number */

            assertTrue(result is Failure)
        }
    }

    @Test
    fun `getGame should return a Game if the game exists`() {
        val gameId = 1
        val userId = 2

        testWithTransactionManagerAndRollback { transactionManager ->
            val gameService = GameService(transactionManager)

            val result = gameService.getGame(gameId, userId)
            assertTrue(result is Success)

            val game = result.successOrNull()!!
            assertEquals(game.id, gameId)
            assertEquals(game.playerBlack, 1)
            assertEquals(game.playerWhite, 2)
            assertEquals(game.rules.ruleId, 1)
        }
    }

    @Test
    fun `getGame should return GameNotFound if the game doesn't exist`() {
        val gameId = 100
        val userId = 2

        testWithTransactionManagerAndRollback { transactionManager ->
            val gameService = GameService(transactionManager)

            val result = gameService.getGame(gameId, userId)

            assertTrue(result is Failure && result.failureOrNull() == GetGameError.GameNotFound)
        }
    }

    @Test
    fun `getGame should return PlayerNotFound if the player doesn't exist`() {
        val gameId = 1
        val userId = Int.MAX_VALUE

        testWithTransactionManagerAndRollback { transactionManager ->
            val gameService = GameService(transactionManager)

            val result = gameService.getGame(gameId, userId)

            assertTrue(result is Failure && result.failureOrNull() == GetGameError.PlayerNotFound)
        }
    }

    @Test
    fun `getGame should return PlayerNotInGame if the game doesn't exist`() {
        val gameId = 1
        val userId = 5

        testWithTransactionManagerAndRollback { transactionManager ->
            val gameService = GameService(transactionManager)

            val result = gameService.getGame(gameId, userId)

            assertTrue(result is Failure && result.failureOrNull() == GetGameError.PlayerNotInGame)
        }
    }

    @Test
    fun `getUserFinishedGames should return a list of finished games`() {
        val userId = 7

        testWithTransactionManagerAndRollback { transactionManager ->
            val gameService = GameService(transactionManager)
            // before
            // forcing a finished game
            transactionManager.run {
                it.gameRepository.createFinishedGame(7, 8)
            }
            // sut
            val (result, _) = gameService.getUserFinishedGames(0, 10, userId)

            assertTrue(result.isNotEmpty())
        }
    }

    @Test
    fun `getUserFinishedGames should return an empty list if the user has no finished games`() {
        val userId = 2

        testWithTransactionManagerAndRollback { transactionManager ->
            val gameService = GameService(transactionManager)

            val (result, _) = gameService.getUserFinishedGames(0, 10, userId)

            assertTrue(result.isEmpty())
        }
    }

    @Test
    fun `getUserFinishedGames should return an empty list if the user doesn't exist`() {
        val userId = 100

        testWithTransactionManagerAndRollback { transactionManager ->
            val gameService = GameService(transactionManager)

            val (result, _) = gameService.getUserFinishedGames(0, 10, userId)

            assertTrue(result.isEmpty())
        }
    }
}
