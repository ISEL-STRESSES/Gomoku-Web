package gomoku.server.http

import gomoku.server.http.model.TokenResponse
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.Duration
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserTests {

    // One of the very few places where we use property injection
    @LocalServerPort
    var port: Int = 8080

    @Test
    fun `can create an user`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()

        // and: a random user
        val username = newTestUserName()
        val password = "!Kz9iYG$%TcB27f"

        // when: creating an user
        // then: the response is a 201 with a proper Location header
        client.post().uri("/users/create")
            .bodyValue(
                mapOf(
                    "username" to username,
                    "password" to password
                )
            )
            .exchange()

            .expectStatus().isCreated
            .expectHeader().value("Location") {
                assertTrue(it.startsWith("/api/users/"))
            }
    }

    @Test
    fun `can create an user, obtain a token, and access user home, and logout`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").responseTimeout(Duration.ofHours(1)).build()

        // and: a random user
        val username = newTestUserName()
        val password = "!Kz9iYG$%2TcB7f"

        // when: creating an user
        // then: the response is a 201 with a proper Location header
        client.post().uri("/users/create")
            .bodyValue(
                mapOf(
                    "username" to username,
                    "password" to password
                )
            )
            .exchange()
            .expectStatus().isCreated
            .expectHeader().value("Location") {
                assertTrue(it.startsWith("/api/users/"))
            }

        // when: creating a token
        // then: the response is a 200
        val result = client.post().uri("/users/token")
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

        // when: getting the user home with a valid token
        // then: the response is a 200 with the proper representation
        client.get().uri("/users/me")
            .header("Authorization", "Bearer ${result.token}")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("username").isEqualTo(username)

        // when: getting the user home with an invalid token
        // then: the response is a 401 with the proper problem
        client.get().uri("/users/me")
            .header("Authorization", "Bearer ${result.token}-invalid")
            .exchange()
            .expectStatus().isUnauthorized
            .expectHeader().valueEquals("WWW-Authenticate", "bearer")

        // when: revoking the token
        // then: response is a 200
        client.post().uri("/users/logout")
            .header("Authorization", "Bearer ${result.token}")
            .exchange()
            .expectStatus().isOk

        // when: getting the user home with the revoked token
        // then: response is a 401
        client.get().uri("/users/me")
            .header("Authorization", "Bearer ${result.token}")
            .exchange()
            .expectStatus().isUnauthorized
            .expectHeader().valueEquals("WWW-Authenticate", "bearer")
    }

    @Test
    fun `get rule ranking for an existing rule`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()

        // and: a random rule id
        val ruleId = 1

        // when: searching for the ranking of a rule
        // then: the response is a 200
        client.get().uri("/users/ranking/$ruleId")
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun `get rule ranking for a non existing rule`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()

        // and: a random rule id
        val ruleId = 100

        // when: searching for the ranking of a rule
        // then: the response is a 404
        client.get().uri("/users/ranking/$ruleId")
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun `get user stats for an existing user`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()

        // and: a random user id
        val userId = 1

        // when: searching for the stats of a user
        // then: the response is a 200
        client.get().uri("/users/stats/$userId")
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun `get user stats for a non existing user`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()

        // and: a random user id
        val userId = 100

        // when: searching for the stats of a user
        // then: the response is a 404
        client.get().uri("/users/stats/$userId")
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun `get user rule ranking for an existing user and rule`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()

        // and: a random rule id
        val userId = 1
        val ruleId = 1

        // when: searching for the ranking of a user and rule
        // then: the response is a 200
        client.get().uri("/users/ranking/$userId/$ruleId")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("ruleId").isEqualTo(ruleId)
            .jsonPath("gamesPlayed").isEqualTo(5)
            .jsonPath("elo").isEqualTo(1500)
    }

    @Test
    fun `get user rule ranking for an existing user but a non existing rule`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()

        // and: a random rule id
        val userId = 1
        val ruleId = 100

        // when: searching for the ranking of a user and rule
        // then: the response is a 404
        client.get().uri("/users/ranking/$userId/$ruleId")
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun `get user rule ranking for an existing rule but a non existing user`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()

        // and: a random rule id
        val userId = 100
        val ruleId = 1

        // when: searching for the ranking of a user and rule
        // then: the response is a 404
        client.get().uri("/users/ranking/$userId/$ruleId")
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun `get user rule ranking for a non existing user and rule`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()

        // and: a random rule id
        val userId = 100
        val ruleId = 1

        // when: searching for the ranking of a user and rule
        // then: the response is a 404
        client.get().uri("/users/ranking/$userId/$ruleId")
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun `get users ranking for an existing user template and rule`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()

        // and: a random username template and a rule id
        val usernameTemplate = "user1"
        val ruleId = 1

        // when: searching for username template in the ranking of a rule
        // then: the response is a 200
        client.get().uri("/users/ranking/$ruleId/search?username=$usernameTemplate")
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun `get users ranking for an existing user template and a non existing rule`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()

        // and: a random username template and a rule id
        val usernameTemplate = "user1"
        val ruleId = 100

        // when: searching for username template in the ranking of a rule
        // then: the response is 404
        client.get().uri("/users/ranking/$ruleId/search?username=$usernameTemplate")
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun `get users ranking for a non existing user template and an existing rule`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()

        // and: a random username template and a rule id
        val usernameTemplate = "abc"
        val ruleId = 1

        // when: searching for username template in the ranking of a rule
        // then: the response is 200 but with an empty list
        client.get().uri("/users/ranking/$ruleId/search?username=$usernameTemplate")
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun `get user for an existing user id`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()

        // and: a random user id
        val userId = 1

        // when: searching for a user with that user id
        // then: the response is 200
        client.get().uri("/users/$userId")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("uuid").isEqualTo(userId)
            .jsonPath("username").isEqualTo("user1")
    }

    @Test
    fun `get user for a non existing user id`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()

        // and: a random user id
        val userId = 100

        // when: searching for a user with that user id
        // then: the response is 404
        client.get().uri("/users/$userId")
            .exchange()
            .expectStatus().isNotFound
    }

    companion object {
        private fun newTestUserName() = "User${abs(Random.nextLong())}"
    }
}