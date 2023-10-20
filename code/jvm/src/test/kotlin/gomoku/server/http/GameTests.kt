package gomoku.server.http

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
    fun `get finished matches`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()

        // and a authenticated user
        val username = "userTest1"
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


        client.get().uri("/games/","username=$username&password=$password")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.matches").isArray
            .jsonPath("$.matches.length()").isEqualTo(0)
        TODO()
    }

    @Test
    fun `get game details`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()

        TODO()
    }

    @Test
    fun `get available rules`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()

        TODO()
    }

    @Test
    fun `make valid move`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()

        TODO()
    }

    @Test
    fun `make already occupied move`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()

        TODO()
    }

    @Test
    fun `make move out of bounds`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()

        TODO()
    }

    @Test
    fun `make move when not your turn`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()

        TODO()
    }

    @Test
    fun `make move when game is finished`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()

        TODO()
    }

    @Test
    fun `make a move on a non existing game`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()

        TODO()
    }

    @Test
    fun `start matchmaking process`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()

        TODO()
    }

    @Test
    fun `start matchmaking process when already in matchmaking process`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()

        TODO()
    }

    @Test
    fun `leave matchmaking process`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()

        TODO()
    }

    @Test
    fun `get current turn player id`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()

        TODO()
    }

    @Test
    fun `create two users, going to matchmaking, begin the match, make moves, see who won`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").responseTimeout(Duration.ofHours(1)).build()

        val ruleId = 2

        // and an authenticated user
        val username1 = newTestUserName()
        val password = "ByQYP78&j7Aug2" // Random password that uses a caps, a number and a special character
        client.post().uri("/users/create")
            .bodyValue(
                mapOf(
                    "username" to username1,
                    "password" to password
                )
            )
            .exchange()
            .expectStatus().isCreated

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
        client.post().uri("/users/create")
            .bodyValue(
                mapOf(
                    "username" to username2,
                    "password" to password
                )
            )
            .exchange()
            .expectStatus().isCreated

        val tokenUser2 = client.post().uri("/users/token")
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
            .expectBody(Matchmaker::class.java)
            .returnResult()
            .responseBody!!

        assertTrue(game.isMatch)

        makeMove(client, game.id, 0, tokenUser1.token, MakeMoveError.InvalidTurn::class.java)

        val validMove1 = makeMove(client, game.id, 0, tokenUser2.token, OngoingMatch::class.java)

        assertTrue(validMove1.moveContainer.getMoves().size == 1)

        val validMove2 = makeMove(client, game.id, 10, tokenUser1.token, OngoingMatch::class.java)

        assertTrue(validMove2.moveContainer.getMoves().size == 2)

        makeMove(client, game.id, 1, tokenUser2.token, OngoingMatch::class.java)

        makeMove(client, game.id, 20, tokenUser1.token, OngoingMatch::class.java)

        makeMove(client, game.id, 2, tokenUser2.token, OngoingMatch::class.java)

        makeMove(client, game.id, 15, tokenUser1.token, OngoingMatch::class.java)

        makeMove(client, game.id, 3, tokenUser2.token, OngoingMatch::class.java)

        makeMove(client, game.id, 16, tokenUser1.token, OngoingMatch::class.java)

        makeMove(client, game.id, 2, tokenUser2.token, MakeMoveError.AlreadyOccupied::class.java)

        val finished = makeMove(client, game.id, 4, tokenUser2.token, FinishedMatch::class.java)

        assertEquals(finished.playerBlack, finished.getWinnerIdOrNull())

    }

    companion object {
        private fun newTestUserName() = "User${abs(Random.nextLong())}"
    }
}
