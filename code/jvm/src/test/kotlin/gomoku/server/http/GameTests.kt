package gomoku.server.http

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.web.reactive.server.WebTestClient
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
}
