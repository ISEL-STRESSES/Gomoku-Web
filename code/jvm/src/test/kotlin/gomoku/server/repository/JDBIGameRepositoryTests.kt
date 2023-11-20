package gomoku.server.repository

import gomoku.server.domain.game.game.CellColor
import gomoku.server.domain.game.game.GameOutcome
import gomoku.server.domain.game.game.GameState
import gomoku.server.domain.game.game.OngoingGame
import gomoku.server.domain.game.game.move.Move
import gomoku.server.domain.game.game.move.Position
import gomoku.server.domain.game.rules.BoardSize
import gomoku.server.domain.game.rules.OpeningRule
import gomoku.server.domain.game.rules.RuleVariant
import gomoku.server.repository.game.GameRepository
import gomoku.server.repository.game.JDBIGameRepository
import gomoku.server.repository.user.JDBIUserRepository
import gomoku.server.testWithHandleAndRollback
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class JDBIGameRepositoryTests {
    @Test
    fun `createGame persists new game correctly`() = testWithHandleAndRollback { handle ->
        val repo = JDBIGameRepository(handle)

        val gameId = repo.createGame(1, 1, 2)
        val game = repo.getGameById(gameId)

        assertNotNull(game)
        assertEquals(1, game.rules.ruleId)
        assertTrue { game is OngoingGame }
    }

    @Test
    fun `setGameState updates state correctly`() = testWithHandleAndRollback { handle ->
        val repo = JDBIGameRepository(handle)

        val gameId = repo.createGame(1, 1, 2)
        repo.setGameState(gameId, GameState.FINISHED)

        val state = repo.getGameState(gameId)
        assertEquals(GameState.FINISHED, state)
    }

    @Test
    fun `setGameOutcome updates outcome correctly (after game state is finished)`() =
        testWithHandleAndRollback { handle ->
            val repo = JDBIGameRepository(handle)

            val gameId = repo.createGame(1, 1, 2)
            repo.setGameState(gameId, GameState.FINISHED)
            repo.setGameOutcome(gameId, GameOutcome.BLACK_WON)

            val outcome = repo.getGameOutcome(gameId)
            assertEquals(GameOutcome.BLACK_WON, outcome)
        }

    @Test
    fun `setGameOutcome doesn't update outcome if game state is not finished`() = testWithHandleAndRollback { handle ->
        val repo = JDBIGameRepository(handle)

        val gameId = repo.createGame(1, 1, 2)
        repo.setGameOutcome(gameId, GameOutcome.BLACK_WON)

        val outcome = repo.getGameOutcome(gameId)
        assertEquals(null, outcome)
    }

    @Test
    fun `getGameRule retrieves correct rule`() = testWithHandleAndRollback { handle ->
        val repo = JDBIGameRepository(handle)

        val rule = repo.getRuleById(1)
        val gameId = repo.createGame(rule?.ruleId ?: 0, 1, 2)
        val gameRule = repo.getGameRule(gameId)

        assertEquals(rule, gameRule)
    }

    @Test
    fun `getGamePlayers retrieves correct players`() = testWithHandleAndRollback { handle ->
        val repo = JDBIGameRepository(handle)

        val gameId = repo.createGame(1, 1, 2)
        val players = repo.getGamePlayers(gameId)

        assertEquals(Pair(1, 2), players)
    }

    @Test
    fun `addToMoveArray appends move correctly`() = testWithHandleAndRollback { handle ->
        val repo = JDBIGameRepository(handle)

        val gameId = repo.createGame(1, 1, 2)
        repo.addToMoveArray(gameId, 3)

        val moves = repo.getAllMoves(gameId)
        assertTrue(moves.contains(Move(Position(3, 0), CellColor.BLACK))) // Assuming Move has a constructor like this
    }

    @Test
    fun `getAllMoves retrieves all moves`() = testWithHandleAndRollback { handle ->
        val repo = JDBIGameRepository(handle)

        val gameId = repo.createGame(1, 1, 2)
        repo.addToMoveArray(gameId, 3)
        repo.addToMoveArray(gameId, 4)

        val moves = repo.getAllMoves(gameId)
        assertEquals(2, moves.size)
    }

    @Test
    fun `getTurn retrieves correct turn`() = testWithHandleAndRollback { handle ->
        val repo = JDBIGameRepository(handle)

        val gameId = repo.createGame(1, 1, 2)
        repo.addToMoveArray(gameId, 3)

        val turn = repo.getTurn(gameId)
        assertEquals(CellColor.WHITE, turn) // Assuming after 1 move by black, it's white's turn
    }

    @Test
    fun `getRuleById gets correctly and doesn't get non-existing, and getAllRules`() =
        testWithHandleAndRollback { handle ->

            val repo = JDBIGameRepository(handle)

            val rule1 = repo.getRuleById(1)
            assertNotNull(rule1)

            val rule4 = repo.getRuleById(4)
            assertNull(rule4)

            val rules = repo.getAllRules()
            assertNotNull(rules)
            assertEquals(3, rules.size)
        }

    @Test
    fun `check base game flow`() = testWithHandleAndRollback { handle ->

        val repo = JDBIGameRepository(handle)
        val uRepo = JDBIUserRepository(handle)

        val gameIdNull = repo.getGameById(31)
        assertNull(gameIdNull)

        val player1 = uRepo.getUserById(1)
        assertNotNull(player1)
        val player2 = uRepo.getUserById(2)
        assertNotNull(player2)

        val gameId = repo.createGame(1, player1.uuid, player2.uuid)

        val gameState = repo.getGameState(gameId)
        assertEquals(GameState.ONGOING, gameState)

        val gameOutcome = repo.getGameOutcome(gameId)
        assertNull(gameOutcome)
        repo.setGameOutcome(gameId, GameOutcome.BLACK_WON)
        val nullGameOutcome = repo.getGameOutcome(gameId)
        assertNull(nullGameOutcome)

        repo.setGameState(gameId, GameState.FINISHED)
        val newGameState = repo.getGameState(gameId)
        assertEquals(GameState.FINISHED, newGameState)

        repo.setGameOutcome(gameId, GameOutcome.BLACK_WON)
        val newGameOutcome = repo.getGameOutcome(gameId)
        assertNotNull(newGameOutcome)
        assertEquals(GameOutcome.BLACK_WON, newGameOutcome)

        val gameRule = repo.getGameRule(gameId)
        assertNotNull(gameRule)
        assertEquals(1, gameRule.ruleId)
        assertEquals(BoardSize.X15, gameRule.boardSize)
        assertEquals(OpeningRule.FREE, gameRule.openingRule)
        assertEquals(RuleVariant.STANDARD, gameRule.variant)

        val gamePlayers = repo.getGamePlayers(gameId)
        assertNotNull(gamePlayers)
        assertEquals(1, gamePlayers.first)
        assertEquals(2, gamePlayers.second)
    }

    @Test
    fun `make moves and get moves`() = testWithHandleAndRollback { handle ->
        val repo = JDBIGameRepository(handle)

        val moves1 = repo.getAllMoves(6)
        assertEquals(2, moves1.size)

        val makeMove = repo.addToMoveArray(6, 3)
        assertTrue(makeMove)

        val moves2 = repo.getAllMoves(6)
        assertEquals(3, moves2.size)

        val getTurn = repo.getTurn(6)
        assertEquals(CellColor.WHITE, getTurn)

        val getLastNMoves = repo.getLastNMoves(6, 1)
        assertEquals(1, getLastNMoves.size)
        assertEquals(CellColor.BLACK, getLastNMoves[0].cellColor)
        assertEquals(Position(3, 0), getLastNMoves[0].position)
    }

    @Test
    fun `isRuleStoredById returns true if rule exists`() = testWithHandleAndRollback { handle ->
        val repo = JDBIGameRepository(handle)

        val ruleExists = repo.isRuleStoredById(1)
        assertTrue(ruleExists)
    }

    @Test
    fun `isRuleStoredById returns false if rule doesn't exist`() = testWithHandleAndRollback { handle ->
        val repo = JDBIGameRepository(handle)

        val ruleExists = repo.isRuleStoredById(666)
        assertFalse(ruleExists)
    }

    @Test
    fun `getUserFinishedGames returns only finished games for user`() = testWithHandleAndRollback { handle ->
        val repo = JDBIGameRepository(handle)

        val userId = 1
        val otherPlayerId = 2
        val anotherPlayerId = 3

        val finishedGameId1 = repo.createFinishedGame(userId, otherPlayerId)
        val finishedGameId2 = repo.createFinishedGame(userId, anotherPlayerId)
        val ongoingGameId = repo.createGame(1, userId, otherPlayerId)

        val games = repo.getUserFinishedGames(0, 10, userId)

        assertTrue(games.any { it.id == finishedGameId1 })
        assertTrue(games.any { it.id == finishedGameId2 })
        assertFalse(games.any { it.id == ongoingGameId })
    }

    @Test
    fun `getUserFinishedGames respects pagination`() = testWithHandleAndRollback { handle ->
        val repo = JDBIGameRepository(handle)

        val userId = 1
        val otherPlayerId = 2
        val anotherPlayerId = 3

        // Setup: Create multiple finished games for the user.
        repo.createFinishedGame(userId, otherPlayerId)
        repo.createFinishedGame(userId, anotherPlayerId)
        repo.createFinishedGame(userId, anotherPlayerId)
        repo.createFinishedGame(userId, anotherPlayerId)

        val gamesPage1 = repo.getUserFinishedGames(0, 2, userId)
        val gamesPage2 = repo.getUserFinishedGames(2, 2, userId)

        assertEquals(2, gamesPage1.size)
        assertEquals(2, gamesPage2.size)
        assertTrue(gamesPage1[0].id != gamesPage2[0].id)
        assertTrue(gamesPage1[1].id != gamesPage2[1].id)
    }
}

fun GameRepository.createFinishedGame(userId: Int, opponentId: Int): Int {
    // Create an ongoing game first
    val gameId = this.createGame(2, userId, opponentId)

    // Set the game to finished
    this.setGameState(gameId, GameState.FINISHED)
    // Setting the outcome to BLACK_WON just for coherence
    this.setGameOutcome(gameId, GameOutcome.BLACK_WON)

    return gameId
}
