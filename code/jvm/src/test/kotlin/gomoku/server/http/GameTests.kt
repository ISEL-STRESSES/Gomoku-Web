package gomoku.server.http

import gomoku.server.deleteLobbies
import gomoku.server.domain.game.Matchmaker
import gomoku.server.domain.game.match.Match
import gomoku.server.domain.game.rules.Rules
import gomoku.server.http.controllers.game.models.FinishedMatchOutputModel
import gomoku.server.http.controllers.game.models.OngoingMatchOutputModel
import gomoku.server.http.model.TokenResponse
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
        assertTrue(rules.isNotEmpty())
    }

    @Test
    fun `get finished matches from a newly created user`() {
        // given: an HTTP client
        val client = WebTestClient
            .bindToServer()
            .baseUrl("http://localhost:$port/api")
            .responseTimeout(Duration.ofHours(1))
            .build()

        // and an authenticated user
        val username = newTestUserName()
        val userId = createUserAndGetId(client, username)
        // and a token
        val userToken = getToken(client, username)

        // when: the user tries to get the finished matches
        val finishedGames = getFinishedGames(client, userToken.token)

        // then: assert that a user doesn't have any finished matches
        assertTrue(finishedGames.isEmpty())
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
        val userId1 = createUserAndGetId(client, username1)
        val tokenUser1 = getToken(client, username1).token

        val username2 = newTestUserName()
        val userId2 = createUserAndGetId(client, username2)
        val tokenUser2 = getToken(client, username2).token

        // and a ruleId
        val ruleId = 2

        // and a game
        startMatchmakingProcess(client, ruleId, tokenUser1)
        val game = startMatchmakingProcess(client, ruleId, tokenUser2)

        // when: the user tries to get the game details
        val gameDetails = getGameDetails(client, game.id, tokenUser1)

        // then: assert that the game details are correct
        assertEquals(gameDetails.ruleId, ruleId)
        assertNotEquals(gameDetails.playerWhite, gameDetails.playerBlack)
        assertTrue(gameDetails.moves.isEmpty())
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
        val userId1 = createUserAndGetId(client, username1)
        val tokenUser1 = getToken(client, username1).token

        val username2 = newTestUserName()
        val userId2 = createUserAndGetId(client, username2)
        val tokenUser2 = getToken(client, username2).token

        // and a ruleId
        val ruleId = 2

        // and a game
        startMatchmakingProcess(client, ruleId, tokenUser1)
        val game = startMatchmakingProcess(client, ruleId, tokenUser2)

        // when: the user tries to make a valid move
        val validMove = makeMove(client, game.id, 0, tokenUser1, OngoingMatchOutputModel::class.java)

        // then: assert that the move is valid
        assertTrue(validMove.moves.size == 1)
    }

    @Test
    fun `make already occupied move`() { // TODO: fix this test
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
        val userId1 = createUserAndGetId(client, username1)
        val tokenUser1 = getToken(client, username1).token

        val username2 = newTestUserName()
        val userId2 = createUserAndGetId(client, username2)
        val tokenUser2 = getToken(client, username2).token

        // and a ruleId
        val ruleId = 2

        // and a game
        startMatchmakingProcess(client, ruleId, tokenUser1)
        val game = startMatchmakingProcess(client, ruleId, tokenUser2)

        // and a valid move
        makeMove(client, game.id, 0, tokenUser1, OngoingMatchOutputModel::class.java) // AQUI

        // when: the user tries to make an already occupied move
        // then: assert that the move is already occupied
        makeMove(client, game.id, 0, tokenUser2, MakeMoveError.AlreadyOccupied::class.java)
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
        val userId1 = createUserAndGetId(client, username1)
        val tokenUser1 = getToken(client, username1).token

        val username2 = newTestUserName()
        val userId2 = createUserAndGetId(client, username2)
        val tokenUser2 = getToken(client, username2).token

        // and a ruleId
        val ruleId = 2

        // and a game
        startMatchmakingProcess(client, ruleId, tokenUser1)
        val game = startMatchmakingProcess(client, ruleId, tokenUser2)

        // when: the user tries to make a move out of bounds
        // then: assert that the move is out of bounds
        makeMove(client, game.id, 1000, tokenUser1, MakeMoveError.ImpossiblePosition::class.java)
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
        val userId1 = createUserAndGetId(client, username1)
        val tokenUser1 = getToken(client, username1).token

        val username2 = newTestUserName()
        val userId2 = createUserAndGetId(client, username2)
        val tokenUser2 = getToken(client, username2).token

        // and a ruleId
        val ruleId = 2

        // and a game
        startMatchmakingProcess(client, ruleId, tokenUser1)
        val game = startMatchmakingProcess(client, ruleId, tokenUser2)

        // when: the user tries to make a move when it's not his turn
        // then: assert that the move is invalid
        makeMove(client, game.id, 0, tokenUser2, MakeMoveError.InvalidTurn::class.java)
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
        val userId1 = createUserAndGetId(client, username1)
        val tokenUser1 = getToken(client, username1).token

        val username2 = newTestUserName()
        val userId2 = createUserAndGetId(client, username2)
        val tokenUser2 = getToken(client, username2).token

        // when: the user tries to make a move on a non existing game
        // then: assert that the move is invalid
        makeMove(client, 1000, 0, tokenUser1, MakeMoveError.GameNotFound::class.java)
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
        val userId = createUserAndGetId(client, username)
        val tokenUser = getToken(client, username).token
        // and an empty lobby
        deleteLobbies()

        // when: the user tries to start a matchmaking process
        val lobby = startMatchmakingProcess(client, ruleId, tokenUser)

        // then: assert that the user is in a lobby
        assertFalse(lobby.isMatch)
    }

    @Test
    fun `start matchmaking process when already in matchmaking process`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()
        // a ruleId
        val ruleId = 2
        // information to create a user
        val username = newTestUserName()
        val userId = createUserAndGetId(client, username)
        val tokenUser = getToken(client, username).token
        // and an empty lobby
        deleteLobbies()
        // and the user already started a matchmaking process
        val lobby = startMatchmakingProcess(client, ruleId, tokenUser)

        // when, then: the user tries to start a matchmaking process again should return a bad request
        client.post().uri("/game/$ruleId")
            .header("Authorization", "Bearer $tokenUser")
            .exchange()
            .expectStatus().isBadRequest
            .expectBody(Matchmaker::class.java)
            .returnResult()
            .responseBody!!
    }

    @Test
    fun `leave matchmaking process`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()
        // a ruleId
        val ruleId = 2
        // information to create a user
        val username = newTestUserName()
        val userId = createUserAndGetId(client, username)
        val tokenUser = getToken(client, username).token
        // and an empty lobby
        deleteLobbies()
        // and the user already started a matchmaking process
        val lobby = startMatchmakingProcess(client, ruleId, tokenUser)

        // when, then: the user tries to leave the matchmaking process is successful
        assertTrue(leaveLobby(client, lobby.id, tokenUser))
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
        val userId1 = createUserAndGetId(client, username1)
        val tokenUser1 = getToken(client, username1).token

        val username2 = newTestUserName()
        val userId2 = createUserAndGetId(client, username2)
        val tokenUser2 = getToken(client, username2).token

        // and a ruleId
        val ruleId = 2

        // and a game
        startMatchmakingProcess(client, ruleId, tokenUser1)
        val game = startMatchmakingProcess(client, ruleId, tokenUser2)

        // when: the user tries to get the current turn player id
        val currentTurnPlayerId = getTurnFromMatch(client, game.id)

        // then: assert that the current turn player id is on the match
        assertTrue((currentTurnPlayerId == userId1) or (currentTurnPlayerId == userId2))

        // and the turn is for player black
        assertEquals(currentTurnPlayerId, getGameDetails(client, game.id, tokenUser1).playerBlack)
    }

    @Test
    fun `create two users, going to matchmaking, begin the match, make moves, see who won`() {
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
        val userId1 = createUserAndGetId(client, username1)
        val tokenUser1 = getToken(client, username1)

        val username2 = newTestUserName()
        val userId2 = createUserAndGetId(client, username2)
        val tokenUser2 = getToken(client, username2)

        // and a ruleId
        val ruleId = 2

        // and a game
        startMatchmakingProcess(client, ruleId, tokenUser1.token)
        val game = startMatchmakingProcess(client, ruleId, tokenUser2.token)
        assertTrue(game.isMatch)

        // and a turn
        val turn = getTurnFromMatch(client, game.id)

        // and player Black and player White
        val getPlayerBlack = if (turn == userId1) tokenUser1.token else tokenUser2.token
        val getPlayerWhite = if (turn == userId1) tokenUser2.token else tokenUser1.token

        // when: the user tries to make a move when it's not his turn
        // then: assert that the move is invalid
        makeMove(client, game.id, 9, getPlayerWhite, MakeMoveError.InvalidTurn::class.java)

        // when: the user tries to make a valid move
        val validMove1 = makeMove(client, game.id, 0, getPlayerBlack, OngoingMatchOutputModel::class.java)
        // then: assert that the move is valid
        assertTrue(validMove1.moves.size == 1)

        // when: the user tries to make a valid move
        val validMove2 = makeMove(client, game.id, 10, getPlayerWhite, OngoingMatchOutputModel::class.java)
        // then: assert that the move is valid
        assertTrue(validMove2.moves.size == 2)

        // when: the users play valid moves till 'almost' the end of the game
        makeMove(client, game.id, 1, getPlayerBlack, OngoingMatchOutputModel::class.java)
        makeMove(client, game.id, 20, getPlayerWhite, OngoingMatchOutputModel::class.java)
        makeMove(client, game.id, 2, getPlayerBlack, OngoingMatchOutputModel::class.java)
        makeMove(client, game.id, 15, getPlayerWhite, OngoingMatchOutputModel::class.java)
        makeMove(client, game.id, 3, getPlayerBlack, OngoingMatchOutputModel::class.java)
        makeMove(client, game.id, 16, getPlayerWhite, OngoingMatchOutputModel::class.java)

        // when: the user tries to make a valid winning move
        val finished = makeMove(client, game.id, 4, getPlayerBlack, FinishedMatchOutputModel::class.java)

        // then: assert that was a winning move and the winner is the player black
        // assertEquals(finished.playerBlack, finished.matchOutcome)
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
        val userId1 = createUserAndGetId(client, username1)
        val tokenUser1 = getToken(client, username1).token

        val username2 = newTestUserName()
        val userId2 = createUserAndGetId(client, username2)
        val tokenUser2 = getToken(client, username2).token

        // and a ruleId
        val ruleId = 2

        // and a game
        startMatchmakingProcess(client, ruleId, tokenUser1)
        val game = startMatchmakingProcess(client, ruleId, tokenUser2)

        // and a valid finished game
        makeMove(client, game.id, 0, tokenUser1, OngoingMatchOutputModel::class.java)
        makeMove(client, game.id, 10, tokenUser2, OngoingMatchOutputModel::class.java)
        makeMove(client, game.id, 1, tokenUser1, OngoingMatchOutputModel::class.java)
        makeMove(client, game.id, 11, tokenUser2, OngoingMatchOutputModel::class.java)
        makeMove(client, game.id, 2, tokenUser1, OngoingMatchOutputModel::class.java)
        makeMove(client, game.id, 12, tokenUser2, OngoingMatchOutputModel::class.java)
        makeMove(client, game.id, 3, tokenUser1, OngoingMatchOutputModel::class.java)
        makeMove(client, game.id, 13, tokenUser2, OngoingMatchOutputModel::class.java)
        makeMove(client, game.id, 4, tokenUser1, FinishedMatchOutputModel::class.java)

        // when: the user tries to make a move when the game is finished
        // then: assert that the move is invalid
        makeMove(client, game.id, 17, tokenUser2, MakeMoveError.GameFinished::class.java)
    }

    /**
     * Util function:
     *
     * Starts the matchmaking process for a game, either
     * by creating a new lobby or joining a match
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
     * @param pos The position of the move
     * @param token The token of the user
     * @param expectedType The expected type of the response
     * @return The result of the move
     */
    private fun <T : Any>makeMove(client: WebTestClient, gameId: Int, pos: Int, token: String, expectedType: Class<T>): T {
        return client.post().uri("/game/$gameId/play?pos=$pos")
            .header("Authorization", "Bearer $token")
            .exchange()
            .expectBody(expectedType).also { println("make move: " + it.returnResult().responseBody) }
            .returnResult()
            .responseBody!!
    }

    /**
     * Util function:
     *
     * Creates a user and returns its id
     * @param client the web test client
     * @param username The username of the user
     * @param password The password of the user
     * @return The id of the user
     */
    private fun createUserAndGetId(client: WebTestClient, username: String): Int {
        var userId: Int? = null
        client.post().uri("/users/create")
            .bodyValue(
                mapOf(
                    "username" to username,
                    "password" to "ByQYP78&j7Aug2"
                )
            )
            .exchange()
            .expectStatus().isCreated
            .expectHeader().value("Location") {
                assertTrue(it.startsWith("/api/users/"))
                userId = it.substringAfterLast("/").toInt()
            }
        return userId!!
    }

    /**
     * Util function:
     *
     * Gets the token of a user
     * @param client the web test client
     * @param username The username of the user
     * @return The token of the user
     */
    private fun getToken(client: WebTestClient, username: String): TokenResponse =
        client.post().uri("/users/token")
            .bodyValue(
                mapOf(
                    "username" to username,
                    "password" to "ByQYP78&j7Aug2"
                )
            )
            .exchange()
            .expectStatus().isOk
            .expectBody(TokenResponse::class.java)
            .returnResult()
            .responseBody!!

    /**
     * Util function:
     *
     * Gets the finished games of a user
     * @param client the web test client
     * @param token The token of the user
     * @return The list of finished games
     */
    private fun getFinishedGames(client: WebTestClient, token: String): List<Match> =
        client.get().uri("/game/").header("Authorization", "Bearer $token")
            .exchange()
            .expectStatus().isOk
            .expectBody<List<Match>>()
            .returnResult()
            .responseBody!!

    /**
     * Util function:
     *
     * Gets the available rules
     * @param client the web test client
     * @return The list of rules
     */
    private fun getRules(client: WebTestClient): List<Rules> =
        client.get().uri("/game/rules")
            .exchange()
            .expectStatus().isOk
            .expectBody<List<Rules>>()
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
            .expectBody<Boolean>()
            .returnResult()
            .responseBody!!

    private fun getGameDetails(client: WebTestClient, gameId: Int, token: String) =
        client.get().uri("/game/$gameId")
            .header("Authorization", "Bearer $token")
            .exchange()
            .expectStatus().isOk
            .expectBody<OngoingMatchOutputModel>()
            .returnResult()
            .responseBody!!

    /**
     * Util function:
     *
     * Gets the current turn player id
     * @param webTestClient the web test client
     * @param matchId The id of the match
     * @return The current turn player id
     */
    private fun getTurnFromMatch(webTestClient: WebTestClient, matchId: Int) =
        webTestClient.get().uri("/game/$matchId/turn")
            .exchange()
            .expectStatus().isOk
            .expectBody<Int>()
            .returnResult()
            .responseBody!!

    companion object {
        private fun newTestUserName() = "User${abs(Random.nextLong())}"
    }
}
