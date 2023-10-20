package gomoku.server.http

import gomoku.server.domain.game.Matchmaker
import gomoku.server.domain.game.match.FinishedMatch
import gomoku.server.domain.game.match.Match
import gomoku.server.domain.game.match.OngoingMatch
import gomoku.server.domain.game.rules.RulesRepresentation
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
    fun `get finished matches`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").responseTimeout(Duration.ofHours(1)).build()

        // and an authenticated user
        val username = newTestUserName()
        val password = "ByQYP78&j7Aug2" // Random password that uses a caps, a number and a special character
        client.post().uri("/users/create")
            .bodyValue(
                mapOf(
                    "username" to username,
                    "password" to password
                )
            )
            .exchange()
            .expectHeader().value("Location") {
                assertTrue(it.startsWith("/api/users/"))
                it.substringAfterLast("/").toInt()
            }

        // and a token
        val userToken = client.post().uri("/users/token")
            .bodyValue(
                mapOf(
                    "username" to username,
                    "password" to password
                )
            )
            .exchange()
            .expectStatus().isOk
            .expectBody(TokenResponse::class.java)
            .returnResult()
            .responseBody!!

        // assert that a user doesn't have any finished matches
        assertTrue(
            client.get().uri("/game/").header("Authorization", "Bearer ${userToken.token}")
                .exchange()
                .expectStatus().isOk
                .expectBody<List<Match>>()
                .returnResult()
                .responseBody!!
                .isEmpty()
        )
    }

    @Test
    fun `get game details`() {
        // given: an HTTP client
//        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()
//
//        TODO()
    }

    @Test
    fun `get available rules`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()

        assertTrue(
            client.get().uri("/game/rules")
                .exchange()
                .expectStatus().isOk
                .expectBody<List<RulesRepresentation>>()
                .returnResult()
                .responseBody!!
                .isNotEmpty()
        )
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
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()
        val ruleId = 1
        val username = newTestUserName()
        val password = "ByQYP78&j7Aug2" // Random password that uses a caps, a number and a special character
        val (lobbyId, token) = startMatchmakingProcess(client, username, password, ruleId)

        client.post().uri("/game/$lobbyId/leave").header("Authorization", "Bearer $token")
            .exchange()
            .expectStatus().isOk
            .expectBody<Boolean>()
            .returnResult()
            .responseBody!!
    }

    @Test
    fun `start matchmaking process when already in matchmaking process`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()
        // a ruleId
        val ruleId = 2
        // information to create a user
        val username = newTestUserName()
        val password = "ByQYP78&j7Aug2"
        val (_, token) = startMatchmakingProcess(client, username, password, ruleId)
        // when: the user tries to start a matchmaking process again should return a bad request
        client.post().uri("/game/$ruleId")
            .header("Authorization", "Bearer $token")
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
        val username = newTestUserName()
        val password = "ByQYP78&j7Aug2"
        val ruleId = 2

        val (lobbyId, token) = startMatchmakingProcess(client, username, password, ruleId)

        assertTrue(
            client.post().uri("/game/$lobbyId/leave")
                .header("Authorization", "Bearer $token")
                .exchange()
                .expectStatus().isOk
                .expectBody<Boolean>()
                .returnResult()
                .responseBody!!
        )
    }

    @Test
    fun `get current turn player id`() {
        // given: an HTTP client
//        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()
//
//        TODO()
    }

    @Test
    fun `create two users, going to matchmaking, begin the match, make moves, see who won`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").responseTimeout(Duration.ofHours(1)).build()

        val ruleId = 2

        // and an authenticated user
        val username1 = newTestUserName()
        val password = "ByQYP78&j7Aug2" // Random password that uses a caps, a number and a special character
        var userId1: Int? = null
        client.post().uri("/users/create")
            .bodyValue(
                mapOf(
                    "username" to username1,
                    "password" to password
                )
            )
            .exchange()
            .expectStatus().isCreated
            .expectHeader().value("Location") {
                assertTrue(it.startsWith("/api/users/"))
                println("player 1 $it")
                userId1 = it.substringAfterLast("/").toInt()
            }

        requireNotNull(userId1)

        val tokenUser1 = client.post().uri("/users/token")
            .bodyValue(
                mapOf(
                    "username" to username1,
                    "password" to password
                )
            )
            .exchange()
            .expectStatus().isOk
            .expectBody(TokenResponse::class.java)
            .returnResult()
            .responseBody!!

        val username2 = newTestUserName()
        var userId2: Int? = null
        client.post().uri("/users/create")
            .bodyValue(
                mapOf(
                    "username" to username2,
                    "password" to password
                )
            )
            .exchange()
            .expectStatus().isCreated
            .expectHeader().value("Location") {
                assertTrue(it.startsWith("/api/users/"))
                println("player 2 $it")
                userId2 = it.substringAfterLast("/").toInt()
            }
        requireNotNull(userId2)

        val tokenUser2 = client.post().uri("/users/token")
            .bodyValue(
                mapOf(
                    "username" to username2,
                    "password" to password
                )
            )
            .exchange()
            .expectStatus().isOk
            .expectBody(TokenResponse::class.java)
            .returnResult()
            .responseBody!!

        // and a game
        val lobby1 = client.post().uri("/game/$ruleId")
            .header("Authorization", "Bearer ${tokenUser1.token}")
            .exchange()
            .expectStatus().isOk
            .expectBody(Matchmaker::class.java)
            .returnResult()
            .responseBody!!

        assertTrue(!lobby1.isMatch)

        val didLeave = client.post().uri("/game/${lobby1.id}/leave")
            .header("Authorization", "Bearer ${tokenUser1.token}")
            .exchange()
            .expectStatus().isOk
            .expectBody<Boolean>()
            .returnResult()
            .responseBody!!

        assertTrue(didLeave)

        val lobby2 = client.post().uri("/game/$ruleId")
            .header("Authorization", "Bearer ${tokenUser2.token}")
            .exchange()
            .expectStatus().isOk
            .expectBody(Matchmaker::class.java)
            .returnResult()
            .responseBody!!

        assertTrue(!lobby2.isMatch)

        val game = client.post().uri("/game/$ruleId")
            .header("Authorization", "Bearer ${tokenUser1.token}")
            .exchange()
            .expectStatus().isOk
            .expectBody(Matchmaker::class.java) // .also { println("game details" + it.returnResult()) }
            .returnResult()
            .responseBody!!

        val turn = client.get().uri("/game/${game.id}/turn")
            .exchange()
            .expectStatus().isOk
            .expectBody<Int>()
            .returnResult()
            .responseBody!!

        val getPlayerBlack = if (turn == userId1) tokenUser1.token else tokenUser2.token
        val getPlayerWhite = if (turn == userId1) tokenUser2.token else tokenUser1.token

        assertTrue(game.isMatch)

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

    private fun startMatchmakingProcess(client: WebTestClient, username: String, password: String, ruleId: Int): Pair<Int, String> {
        client.post().uri("/users/create")
            .bodyValue(
                mapOf(
                    "username" to username,
                    "password" to password
                )
            )
            .exchange()
            .expectStatus().isCreated

        val tokenUser = client.post().uri("/users/token")
            .bodyValue(
                mapOf(
                    "username" to username,
                    "password" to password
                )
            )
            .exchange()
            .expectStatus().isOk
            .expectBody(TokenResponse::class.java)
            .returnResult()
            .responseBody!!

        val lobby = client.post().uri("/game/$ruleId")
            .header("Authorization", "Bearer ${tokenUser.token}")
            .exchange()
            .expectStatus().isOk
            .expectBody(Matchmaker::class.java)
            .returnResult()
            .responseBody!!

        assertFalse(lobby.isMatch)

        return lobby.id to tokenUser.token
    }

    private fun <T : Any>makeMove(client: WebTestClient, gameId: Int, pos: Int, token: String, expectedType: Class<T>): T {
        return client.post().uri("/game/$gameId/play?pos=$pos")
            .header("Authorization", "Bearer $token")
            .exchange()
            .expectBody(expectedType)
            .returnResult()
            .responseBody!!
    }

    companion object {
        private fun newTestUserName() = "User${abs(Random.nextLong())}"
    }
}
