package gomoku.server.http

import gomoku.server.domain.game.Matchmaker
import gomoku.server.http.model.TokenResponse
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.web.reactive.server.WebTestClient
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.Test

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
            .exchange().returnResult(Int::class.java)


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
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()

        val ruleId = 2

        // and an authenticated user
        val username1 = newTestUserName()
        val password = "ByQYP78&j7Aug2" // Random password that uses a caps, a number and a special character
        val userId1 = client.post().uri("/users/create")
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
        val userId2 = client.post().uri("/users/create")
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
        val lobby1 = client.post().uri("/game/match/$ruleId?userId=" + 81)
            .exchange()
            .expectStatus().isOk
            .expectBody(Matchmaker::class.java)
            .returnResult()
            .responseBody!!

        println(lobby1.id)
        println(lobby1.isMatch)

        assert(false)

        client.post().uri("/${lobby1}}/leave")
            .header("Authorization", "Bearer ${tokenUser1.token}")
            .exchange()
            .expectStatus().isOk

        val lobby2 = client.post().uri("/game/match/$ruleId?userId=" + 82)
            .exchange()
            .expectStatus().isOk
            .expectBody(Matchmaker::class.java)
            .returnResult()
            .responseBody!!

        assert(!lobby2.isMatch)

        val game = client.post().uri("/game/match/$ruleId?userId=" + 81)
            .exchange()
            .expectStatus().isOk
            .expectBody(Matchmaker::class.java)
            .returnResult()
            .responseBody!!

        assert(game.isMatch)

    }

    companion object {
        private fun newTestUserName() = "User${abs(Random.nextLong())}"
    }
}
