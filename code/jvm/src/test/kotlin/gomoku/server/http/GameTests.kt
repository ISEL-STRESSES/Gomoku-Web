package gomoku.server.http

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import gomoku.server.deleteLobbies
import gomoku.server.domain.game.Matchmaker
import gomoku.server.http.controllers.game.models.GameOutputModel
import gomoku.server.http.controllers.game.models.GetFinishedGamesOutputModel
import gomoku.server.http.controllers.game.models.GetRulesOutputModel
import gomoku.server.http.model.CreateUserResponse
import gomoku.server.http.model.PlayerRuleStatsResponse
import gomoku.server.services.errors.game.MakeMoveError
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import java.time.Duration
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GameTests {

    // One of the very few places where we use property injection
    @LocalServerPort
    var port: Int = 8080

    @Test
    fun `get available rules`() {
        // given: an HTTP client
        val client = WebTestClient
            .bindToServer()
            .baseUrl("http://localhost:$port/api")
            .responseTimeout(Duration.ofHours(1))
            .build()

        // when: the user tries to get the available rules
        val rules = getRules(client)

        // then: assert that there are rules
        assertTrue(rules.rulesList.isNotEmpty())
    }

    @Test
    fun `get finished games from a newly created user`() {
        // given: an HTTP client
        val client = WebTestClient
            .bindToServer()
            .baseUrl("http://localhost:$port/api")
            .responseTimeout(Duration.ofHours(1))
            .build()

        // and an authenticated user
        val username = newTestUserName()
        val createUserResponse = createUserAndGetId(client, username) // userId is not needed

        // when: the user tries to get the finished games
        val finishedGames = getFinishedGames(client, createUserResponse.token)

        // then: assert that a user doesn't have any finished games
        assertTrue(finishedGames.finishedGames.isEmpty())
    }

    @Test
    fun `get game details`() {
        // given: an HTTP client
        val client = WebTestClient
            .bindToServer()
            .baseUrl("http://localhost:$port/api")
            .responseTimeout(Duration.ofHours(1))
            .build()

        // and an empty lobby
        deleteLobbies()

        // and information to create 2 users
        val username1 = newTestUserName()
        val createUserResponse = createUserAndGetId(client, username1) // userId is not needed

        val username2 = newTestUserName()
        val createUserResponse2 = createUserAndGetId(client, username2) // userId is not needed

        // and a ruleId
        val ruleId = 2

        // and a game
        startMatchmakingProcess(client, ruleId, createUserResponse.token)
        val game = startMatchmakingProcess(client, ruleId, createUserResponse2.token)

        // when: the user tries to get the game details
        val gameDetails = getGameDetails(client, game.id, createUserResponse.token, GameOutputModel::class.java)
        assertEquals(gameDetails.type, GameOutputModel.GameType.ONGOING)

        // then: assert that the game details are correct
        assertEquals(gameDetails.rule.ruleId, ruleId)
        assertNotEquals(gameDetails.playerWhite, gameDetails.playerBlack)
        assertTrue(gameDetails.moves.orderOfMoves.isEmpty())
        assertNull(gameDetails.gameOutcome)
    }

    @Test
    fun `make valid move`() {
        // given: an HTTP client
        val client = WebTestClient
            .bindToServer()
            .baseUrl("http://localhost:$port/api")
            .responseTimeout(Duration.ofHours(1))
            .build()

        // and an empty lobby
        deleteLobbies()

        // and information to create 2 users
        val username1 = newTestUserName()
        val createUserResponse = createUserAndGetId(client, username1)

        val username2 = newTestUserName()
        val createUserResponse2 = createUserAndGetId(client, username2) // userId is not needed

        // and a ruleId
        val ruleId = 2

        // and a game
        startMatchmakingProcess(client, ruleId, createUserResponse.token)
        val game = startMatchmakingProcess(client, ruleId, createUserResponse2.token)

        // and a turn
        val getTurn = getTurnFromGame(client, game.id)
        val currentPlayer =
            if (getTurn == createUserResponse.userId) createUserResponse.token else createUserResponse2.token

        // when: the user tries to make a valid move
        val gameAfterValidMove = makeMove(client, game.id, 0, 0, currentPlayer, GameOutputModel::class.java)

        // then: assert that the move is valid
        assertTrue(gameAfterValidMove.moves.orderOfMoves.size == 1)
        assertNull(gameAfterValidMove.gameOutcome)
    }

    @Test
    fun `make already occupied move`() {
        // given: an HTTP client
        val client = WebTestClient
            .bindToServer()
            .baseUrl("http://localhost:$port/api")
            .responseTimeout(Duration.ofHours(1))
            .build()

        // and an empty lobby
        deleteLobbies()

        // and information to create 2 users
        val username1 = newTestUserName()
        val createUserResponse = createUserAndGetId(client, username1)

        val username2 = newTestUserName()
        val createUserResponse2 = createUserAndGetId(client, username2) // userId is not needed

        // and a ruleId
        val ruleId = 2

        // and a game
        startMatchmakingProcess(client, ruleId, createUserResponse.token)
        val game = startMatchmakingProcess(client, ruleId, createUserResponse2.token)

        // and a turn
        val getTurn = getTurnFromGame(client, game.id)
        val currentPlayer =
            if (getTurn == createUserResponse.userId) createUserResponse.token else createUserResponse2.token
        val otherPlayer =
            if (getTurn == createUserResponse.userId) createUserResponse2.token else createUserResponse.token

        // and a valid move
        makeMove(client, game.id, 0, 0, currentPlayer, GameOutputModel::class.java)

        // when: the user tries to make an already occupied move
        // then: assert that the move is already occupied
        makeMove(client, game.id, 0, 0, otherPlayer, MakeMoveError.AlreadyOccupied::class.java)
    }

    @Test
    fun `make move out of bounds`() {
        // given: an HTTP client
        val client = WebTestClient
            .bindToServer()
            .baseUrl("http://localhost:$port/api")
            .responseTimeout(Duration.ofHours(1))
            .build()

        // and an empty lobby
        deleteLobbies()

        // and information to create 2 users
        val username1 = newTestUserName()
        val createUserResponse = createUserAndGetId(client, username1)

        val username2 = newTestUserName()
        val createUserResponse2 = createUserAndGetId(client, username2) // userId is not needed

        // and a ruleId
        val ruleId = 2

        // and a game
        startMatchmakingProcess(client, ruleId, createUserResponse.token)
        val game = startMatchmakingProcess(client, ruleId, createUserResponse2.token)

        // and a turn
        val getTurn = getTurnFromGame(client, game.id)
        val currentPlayer =
            if (getTurn == createUserResponse.userId) createUserResponse.token else createUserResponse2.token

        // when: the user tries to make a move out of bounds
        // then: assert that the move is out of bounds
        makeMove(client, game.id, 23, 23, currentPlayer, MakeMoveError.ImpossiblePosition::class.java)
    }

    @Test
    fun `make move when not your turn`() {
        // given: an HTTP client
        val client = WebTestClient
            .bindToServer()
            .baseUrl("http://localhost:$port/api")
            .responseTimeout(Duration.ofHours(1))
            .build()

        // and an empty lobby
        deleteLobbies()

        // and information to create 2 users
        val username1 = newTestUserName()
        val createUserResponse = createUserAndGetId(client, username1)

        val username2 = newTestUserName()
        val createUserResponse2 = createUserAndGetId(client, username2) // userId is not needed

        // and a ruleId
        val ruleId = 2

        // and a game
        startMatchmakingProcess(client, ruleId, createUserResponse.token)
        val game = startMatchmakingProcess(client, ruleId, createUserResponse2.token)

        // and a turn
        val getTurn = getTurnFromGame(client, game.id)
        val otherPlayer =
            if (getTurn == createUserResponse.userId) createUserResponse2.token else createUserResponse.token

        // when: the user tries to make a move when it's not his turn
        // then: assert that the move is invalid
        makeMove(client, game.id, 0, 0, otherPlayer, MakeMoveError.InvalidTurn::class.java)
    }

    @Test
    fun `make a move on a non existing game`() {
        // given: an HTTP client
        val client = WebTestClient
            .bindToServer()
            .baseUrl("http://localhost:$port/api")
            .responseTimeout(Duration.ofHours(1))
            .build()

        // and an empty lobby
        deleteLobbies()

        // and information to create 2 users
        val username1 = newTestUserName()
        val createUserResponse = createUserAndGetId(client, username1) // userId is not needed

        val username2 = newTestUserName()
        createUserAndGetId(client, username2) // userId is not needed

        // when: the user tries to make a move on a non existing game
        // then: assert that the move is invalid
        makeMove(client, Int.MAX_VALUE, 0, 0, createUserResponse.token, MakeMoveError.GameNotFound::class.java)
    }

    @Test
    fun `start matchmaking process`() {
        // given: an HTTP client
        val client = WebTestClient
            .bindToServer()
            .baseUrl("http://localhost:$port/api")
            .responseTimeout(Duration.ofHours(1))
            .build()

        // a ruleId
        val ruleId = 1

        // information to create a user
        val username = newTestUserName()
        val createUserResponse = createUserAndGetId(client, username) // userId is not needed

        // and an empty lobby
        deleteLobbies()

        // when: the user tries to start a matchmaking process
        val lobby = startMatchmakingProcess(client, ruleId, createUserResponse.token)

        // then: assert that the user is in a lobby
        assertFalse(lobby.isGame)
    }

    @Test
    fun `start matchmaking process when already in matchmaking process`() {
        // given: an HTTP client
        val client = WebTestClient
            .bindToServer()
            .baseUrl("http://localhost:$port/api")
            .responseTimeout(Duration.ofMinutes(1))
            .build()

        // and an empty lobby
        deleteLobbies()

        // a ruleId
        val ruleId = 2

        // information to create a user
        val username = newTestUserName()
        val createUserResponse = createUserAndGetId(client, username) // userId is not needed

        // and the user already started a matchmaking process
        startMatchmakingProcess(client, ruleId, createUserResponse.token) // lobbyId is not needed

        // when, then: the user tries to start a matchmaking process again should return a bad request
        client.post().uri("/game/$ruleId")
            .header("Authorization", "Bearer ${createUserResponse.token}")
            .exchange()
            .expectStatus().isBadRequest
            .expectBody(Matchmaker::class.java)
            .returnResult()
            .responseBody!!
    }

    @Test
    fun `leave matchmaking process`() {
        // given: an HTTP client
        val client = WebTestClient
            .bindToServer()
            .baseUrl("http://localhost:$port/api")
            .responseTimeout(Duration.ofMinutes(1))
            .build()

        // and an empty lobby
        deleteLobbies()

        // a ruleId
        val ruleId = 2

        // information to create a user
        val username = newTestUserName()
        val createUserResponse = createUserAndGetId(client, username) // userId is not needed

        // and the user already started a matchmaking process
        val lobby = startMatchmakingProcess(client, ruleId, createUserResponse.token)

        // when, then: the user tries to leave the matchmaking process is successful
        assertEquals(Unit, leaveLobby(client, lobby.id, createUserResponse.token))
    }

    @Test
    fun `get current turn player id`() {
        // given: an HTTP client
        val client = WebTestClient
            .bindToServer()
            .baseUrl("http://localhost:$port/api")
            .responseTimeout(Duration.ofMinutes(1))
            .build()

        // and an empty lobby
        deleteLobbies()

        // and information to create 2 users
        val username1 = newTestUserName()
        val createUserResponse = createUserAndGetId(client, username1)

        val username2 = newTestUserName()
        val createUserResponse2 = createUserAndGetId(client, username2)

        // and a ruleId
        val ruleId = 2

        // and a game
        startMatchmakingProcess(client, ruleId, createUserResponse.token)
        val game = startMatchmakingProcess(client, ruleId, createUserResponse2.token)

        // when: the user tries to get the current turn player id
        val currentTurnPlayerId = getTurnFromGame(client, game.id)

        // then: assert that the current turn player id is on the game
        assertTrue((currentTurnPlayerId == createUserResponse.userId) or (currentTurnPlayerId == createUserResponse2.userId))

        // and the turn is for player black
        assertEquals(
            currentTurnPlayerId,
            getGameDetails(client, game.id, createUserResponse.token, GameOutputModel::class.java).playerBlack
        )
    }

    @Test
    fun `create two users, going to matchmaking, begin the game, make moves, see who won`() {
        // given: an HTTP client
        val client = WebTestClient
            .bindToServer()
            .baseUrl("http://localhost:$port/api")
            .responseTimeout(Duration.ofHours(1))
            .build()

        // and an empty lobby
        deleteLobbies()

        // and 2 authenticated users
        val username1 = newTestUserName()
        val createUserResponse = createUserAndGetId(client, username1)

        val username2 = newTestUserName()
        val createUserResponse2 = createUserAndGetId(client, username2) // userId is not needed

        // and a ruleId
        val ruleId = 2

        // and a game
        startMatchmakingProcess(client, ruleId, createUserResponse.token)
        val game = startMatchmakingProcess(client, ruleId, createUserResponse2.token)
        assertTrue(game.isGame)

        // and a turn
        val playerBlackId = getTurnFromGame(client, game.id)

        // and player Black and player White
        val getPlayerBlack =
            if (playerBlackId == createUserResponse.userId) createUserResponse.token else createUserResponse2.token
        val getPlayerWhite =
            if (playerBlackId == createUserResponse.userId) createUserResponse2.token else createUserResponse.token

        // when: the user tries to make a move when it's not his turn
        // then: assert that the move is invalid
        makeMove(client, game.id, 9, 9, getPlayerWhite, MakeMoveError.InvalidTurn::class.java)

        // when: the user tries to make a valid move
        val validMove1 = makeMove(client, game.id, 0, 0, getPlayerBlack, GameOutputModel::class.java)
        // then: assert that the move is valid
        assertTrue(validMove1.moves.orderOfMoves.size == 1)
        assertNull(validMove1.gameOutcome)

        // when: the user tries to make a valid move
        val validMove2 = makeMove(client, game.id, 2, 10, getPlayerWhite, GameOutputModel::class.java)
        // then: assert that the move is valid
        assertTrue(validMove2.moves.orderOfMoves.size == 2)
        assertNull(validMove1.gameOutcome)

        // when: the users play valid moves till 'almost' the end of the game
        makeMove(client, game.id, 0, 1, getPlayerBlack, GameOutputModel::class.java)
        makeMove(client, game.id, 11, 11, getPlayerWhite, GameOutputModel::class.java)
        makeMove(client, game.id, 0, 2, getPlayerBlack, GameOutputModel::class.java)
        makeMove(client, game.id, 11, 12, getPlayerWhite, GameOutputModel::class.java)
        makeMove(client, game.id, 0, 3, getPlayerBlack, GameOutputModel::class.java)
        makeMove(client, game.id, 9, 9, getPlayerWhite, GameOutputModel::class.java)

        // when: the user tries to make a valid winning move
        val gameAfterFinishingMove = makeMove(client, game.id, 0, 4, getPlayerBlack, GameOutputModel::class.java)

        println(gameAfterFinishingMove)
        // then: assert that was a winning move and the winner is the player black
        assertEquals("BLACK_WON", gameAfterFinishingMove.gameOutcome)
        assertEquals(playerBlackId, gameAfterFinishingMove.playerBlack)
        assertNull(gameAfterFinishingMove.turn)

        // and assert that the stats of the players are updated
        val playerBlackStats = getPlayerRuleStats(client, playerBlackId, ruleId)
        val playerWhiteId = if (playerBlackId == createUserResponse.userId) createUserResponse2.userId else createUserResponse.userId
        val playerWhiteStats = getPlayerRuleStats(client, playerWhiteId, ruleId)
        assertEquals(1, playerBlackStats.gamesPlayed)
        assertEquals(1, playerWhiteStats.gamesPlayed)
        assertTrue { playerBlackStats.elo > playerWhiteStats.elo }
        assertEquals(ruleId, playerBlackStats.ruleId)
        assertEquals(ruleId, playerWhiteStats.ruleId)
    }

    @Test
    fun `make move when game is finished`() {
        // given: an HTTP client
        val client = WebTestClient
            .bindToServer()
            .baseUrl("http://localhost:$port/api")
            .responseTimeout(Duration.ofHours(1))
            .build()

        // and an empty lobby
        deleteLobbies()

        // and 2 authenticated users
        val username1 = newTestUserName()
        val createUserResponse = createUserAndGetId(client, username1)

        val username2 = newTestUserName()
        val createUserResponse2 = createUserAndGetId(client, username2)

        // and a ruleId
        val ruleId = 2

        // and a game
        startMatchmakingProcess(client, ruleId, createUserResponse.token)
        val game = startMatchmakingProcess(client, ruleId, createUserResponse2.token)

        val turn = getTurnFromGame(client, game.id)

        val playerBlack = if (turn == createUserResponse.userId) createUserResponse.token else createUserResponse2.token
        val playerWhite = if (turn == createUserResponse.userId) createUserResponse2.token else createUserResponse.token

        // and a valid finished game
        makeMove(client, game.id, 0, 0, playerBlack, GameOutputModel::class.java)
        makeMove(client, game.id, 9, 9, playerWhite, GameOutputModel::class.java)
        makeMove(client, game.id, 0, 1, playerBlack, GameOutputModel::class.java)
        makeMove(client, game.id, 11, 11, playerWhite, GameOutputModel::class.java)
        makeMove(client, game.id, 0, 2, playerBlack, GameOutputModel::class.java)
        makeMove(client, game.id, 11, 12, playerWhite, GameOutputModel::class.java)
        makeMove(client, game.id, 0, 3, playerBlack, GameOutputModel::class.java)
        makeMove(client, game.id, 13, 13, playerWhite, GameOutputModel::class.java)
        val finishedGame = makeMove(client, game.id, 0, 4, playerBlack, GameOutputModel::class.java) // winning move
        assertEquals("BLACK_WON", finishedGame.gameOutcome)
        assertNull(finishedGame.turn)

        // when: the user tries to make a move when the game is finished
        // then: assert that the move is invalid
        makeMove(client, game.id, 14, 14, playerWhite, MakeMoveError.GameFinished::class.java)
        makeMove(client, game.id, 8, 10, playerBlack, MakeMoveError.GameFinished::class.java)
    }

    /**
     * Util function:
     *
     * Starts the matchmaking process for a game, either
     * by creating a new lobby or joining a game
     * @param client the web test client
     * @param ruleId The id of the rule
     * @param token The token of the user
     * @return The result of the matchmaking process
     */
    private fun startMatchmakingProcess(client: WebTestClient, ruleId: Int, token: String): Matchmaker =
        client.post().uri("/game/$ruleId")
            .header("Authorization", "Bearer $token")
            .exchange()
            .expectStatus().isOk
            .expectBody(Matchmaker::class.java)
            .returnResult()
            .responseBody!!

    /**
     * Util function:
     *
     * Makes a move on a game, either is valid or not
     * @param client the web test client
     * @param gameId The id of the game
     * @param x The x coordinate of the move
     * @param y The y coordinate of the move
     * @param token The token of the user
     * @param expectedType The expected type of the response
     * @return The result of the move
     */
    private fun <T : Any> makeMove(
        client: WebTestClient,
        gameId: Int,
        x: Int,
        y: Int,
        token: String,
        expectedType: Class<T>
    ): T {
        return client.post().uri("/game/$gameId/play?x=$x&y=$y")
            .header("Authorization", "Bearer $token")
            .exchange()
            .expectBody(expectedType)
            .returnResult()
            .responseBody ?: throw Exception("Response body should not be null")
    }

    /**
     * Util function:
     *
     * Creates a user and returns its id
     * @param client the web test client
     * @param username The username of the user
     * @return The id of the user
     */
    private fun createUserAndGetId(client: WebTestClient, username: String): CreateUserResponse {
        val objectMapper = ObjectMapper().registerKotlinModule()
        var createUserResponse: CreateUserResponse? = null
        client.post().uri("/users/create")
            .bodyValue(
                mapOf(
                    "username" to username,
                    "password" to "ByQYP78&j7Aug2"
                )
            )
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .consumeWith { response ->
                val json = String(response.responseBody!!)
                val node = objectMapper.readTree(json)
                val properties = node.get("properties")
                val userId = properties.get("userId").asInt()
                val token = properties.get("token").asText()
                assertNotNull(userId)
                assertNotNull(token)
                createUserResponse = CreateUserResponse(userId, token)
            }
        checkNotNull(createUserResponse) { "createUserResponse should not be null" }
        return createUserResponse!!
    }

    /**
     * Util function:
     *
     * Gets the finished games of a user
     * @param client the web test client
     * @param token The token of the user
     * @return The list of finished games
     */
    private fun getFinishedGames(client: WebTestClient, token: String): GetFinishedGamesOutputModel =
        client.get().uri("/game/").header("Authorization", "Bearer $token")
            .exchange()
            .expectStatus().isOk
            .expectBody<GetFinishedGamesOutputModel>()
            .returnResult()
            .responseBody!!

    /**
     * Util function:
     *
     * Gets the available rules
     * @param client the web test client
     * @return The list of rules
     */
    private fun getRules(client: WebTestClient): GetRulesOutputModel =
        client.get().uri("/game/rules")
            .exchange()
            .expectStatus().isOk
            .expectBody<GetRulesOutputModel>()
            .returnResult()
            .responseBody!!

    /**
     * Util function:
     *
     * Leaves a lobby
     * @param webTestClient the web test client
     * @param lobbyId The id of the lobby
     * @param userToken The token of the user
     * @return True if the user left the lobby, false otherwise
     */
    private fun leaveLobby(webTestClient: WebTestClient, lobbyId: Int, userToken: String) =
        webTestClient.post().uri("/game/$lobbyId/leave")
            .header("Authorization", "Bearer $userToken")
            .exchange()
            .expectStatus().isOk
            .expectBody<Unit>()
            .returnResult()
            .responseBody!!

    private fun <T : Any> getGameDetails(client: WebTestClient, gameId: Int, token: String, expectedType: Class<T>): T =
        client.get().uri("/game/$gameId")
            .header("Authorization", "Bearer $token")
            .exchange()
            .expectStatus().isOk
            .expectBody(expectedType)
            .returnResult()
            .responseBody!!

    /**
     * Util function:
     *
     * Gets the current turn player id
     * @param webTestClient the web test client
     * @param gameId The id of the game
     * @return The current turn player id
     */
    private fun getTurnFromGame(webTestClient: WebTestClient, gameId: Int) =
        webTestClient.get().uri("/game/$gameId/turn")
            .exchange()
            .expectStatus().isOk
            .expectBody<Int>()
            .returnResult()
            .responseBody!!

    private fun getPlayerRuleStats(webTestClient: WebTestClient, userId: Int, ruleId: Int): PlayerRuleStatsResponse {
        var playerRuleStatsResponse: PlayerRuleStatsResponse? = null
        webTestClient.get().uri("/ranking/$userId/$ruleId")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith { response ->
                val json = String(response.responseBody!!)
                val node = ObjectMapper().readTree(json)
                val properties = node.get("properties")
                val ruleId = properties.get("ruleId").asInt()
                val userId = properties.get("userId").asInt()
                val username = properties.get("username").asText()
                val gamesPlayed = properties.get("gamesPlayed").asInt()
                val elo = properties.get("elo").asInt()
                assertNotNull(userId)
                assertNotNull(username)
                assertNotNull(ruleId)
                assertNotNull(gamesPlayed)
                assertNotNull(elo)
                playerRuleStatsResponse = PlayerRuleStatsResponse(userId, username, ruleId, gamesPlayed, elo)
            }
        requireNotNull(playerRuleStatsResponse)
        return playerRuleStatsResponse!!
    }
    companion object {
        private fun newTestUserName() = "User${abs(Random.nextLong())}"
    }
}
