package gomoku.server.http

import gomoku.server.deleteLobbies
import gomoku.server.domain.game.Matchmaker
import gomoku.server.domain.game.match.FinishedMatch
import gomoku.server.domain.game.match.Match
import gomoku.server.domain.game.match.OngoingMatch
import gomoku.server.domain.game.rules.Rules
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
        assertTrue(rules.isNotEmpty().also { println(it) })
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
//        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()
//
//        TODO()
    }

    @Test
    fun `make valid move`() {
        // given: an HTTP client
        // val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()

        // TODO()
    }

    @Test
    fun `make already occupied move`() {
        // given: an HTTP client
//        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()
//
//        TODO()
    }

    @Test
    fun `make move out of bounds`() {
        // given: an HTTP client
//        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()
//
//        TODO()
    }

    @Test
    fun `make move when not your turn`() {
        // given: an HTTP client
//        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()
//
//        TODO()
    }

    @Test
    fun `make move when game is finished`() {
        // given: an HTTP client
//        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()
//
//        TODO()
    }

    @Test
    fun `make a move on a non existing game`() {
        // given: an HTTP client
//        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()
//
//        TODO()
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
//        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()
//
//        TODO()
    }

    @Test
    fun `create two users, going to matchmaking, begin the match, make moves, see who won`() { // TODO: fix this test
        // before
        deleteLobbies()

        // given: an HTTP client
        val client = WebTestClient
            .bindToServer()
            .baseUrl("http://localhost:$port/api")
            .responseTimeout(Duration.ofHours(1))
            .build()

        // and a ruleId
        val ruleId = 2

        // and 2 authenticated users
        val username1 = newTestUserName()
        val userId1 = createUserAndGetId(client, username1)
        val tokenUser1 = getToken(client, username1)

        val username2 = newTestUserName()
        val userId2 = createUserAndGetId(client, username2)
        val tokenUser2 = getToken(client, username2)

        // and a lobby
        val lobby1 = startMatchmakingProcess(client, ruleId, tokenUser1.token)
        assertTrue(!lobby1.isMatch)

        val didLeave = leaveLobby(client, lobby1.id, tokenUser1.token)
        assertTrue(didLeave)

        val lobby2 = startMatchmakingProcess(client, ruleId, tokenUser2.token)
        assertTrue(!lobby2.isMatch)

        val game = startMatchmakingProcess(client, ruleId, tokenUser1.token)
        assertTrue(game.isMatch)

        val turn = getTurnFromMatch(client, game.id)

        val getPlayerBlack = if (turn == userId1) tokenUser1.token else tokenUser2.token
        val getPlayerWhite = if (turn == userId1) tokenUser2.token else tokenUser1.token

        println("token player black: $getPlayerBlack")
        println("token player white: $getPlayerWhite")

        makeMove(client, game.id, 9, getPlayerWhite, MakeMoveError.InvalidTurn::class.java)

        val validMove1 = makeMove(client, game.id, 0, getPlayerBlack, OngoingMatch::class.java) // AQUI

        assertTrue(validMove1.moveContainer.getMoves().size == 1)

        val validMove2 = makeMove(client, game.id, 10, getPlayerWhite, OngoingMatch::class.java)

        assertTrue(validMove2.moveContainer.getMoves().size == 2)

        makeMove(client, game.id, 1, getPlayerBlack, OngoingMatch::class.java)

        makeMove(client, game.id, 20, getPlayerWhite, OngoingMatch::class.java)

        makeMove(client, game.id, 2, getPlayerBlack, OngoingMatch::class.java)

        makeMove(client, game.id, 15, getPlayerWhite, OngoingMatch::class.java)

        makeMove(client, game.id, 3, getPlayerBlack, OngoingMatch::class.java)

        makeMove(client, game.id, 16, getPlayerWhite, OngoingMatch::class.java)

        makeMove(client, game.id, 2, getPlayerBlack, MakeMoveError.AlreadyOccupied::class.java)

        val finished = makeMove(client, game.id, 4, getPlayerBlack, FinishedMatch::class.java)

        assertEquals(finished.playerBlack, finished.getWinnerIdOrNull())
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
