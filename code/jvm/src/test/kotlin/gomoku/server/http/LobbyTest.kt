package gomoku.server.http

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import gomoku.server.deleteLobbies
import gomoku.server.http.model.toGameResponse
import org.junit.jupiter.api.Assertions.assertNull
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.Duration
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LobbyTest {
    @LocalServerPort
    var port: Int = 8080

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

        // information to create a user
        val username = newTestUserName()
        val createUserResponse = createUserAndGetId(client, username) // userId is not needed

        // and the user already started a matchmaking process
        val lobby = startMatchmakingProcess(client, createUserResponse.token).getProperties()

        // when, then: the user tries to leave the matchmaking process is successful
        assertEquals(Unit, leaveLobby(client, lobby.path("id").asInt(), createUserResponse.token))
    }

    @Test
    fun `leave matchmaking process with the wrong user`() {
        // given: an HTTP client
        val client = WebTestClient
            .bindToServer()
            .baseUrl("http://localhost:$port/api")
            .responseTimeout(Duration.ofMinutes(1))
            .build()

        // and an empty lobby
        deleteLobbies()

        // information to create 2 user
        val username = newTestUserName()
        val createUserResponse = createUserAndGetId(client, username) // userId is not needed

        // and the user already started a matchmaking process
        val lobby = startMatchmakingProcess(client, createUserResponse.token).getProperties()

        // and another user
        val anotherUsername = newTestUserName()
        val anotherCreateUserResponse = createUserAndGetId(client, anotherUsername) // userId is not needed

        // when, then: the user tries to leave the matchmaking process is successful
        client.post().uri("/lobby/${lobby.path("id").asInt()}/leave")
            .header("Authorization", "bearer ${anotherCreateUserResponse.token}")
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .returnResult()
            .responseBody!!
    }

    @Test
    fun `leave matchmaking process with the wrong lobby id`() {
        // given: an HTTP client
        val client = WebTestClient
            .bindToServer()
            .baseUrl("http://localhost:$port/api")
            .responseTimeout(Duration.ofMinutes(1))
            .build()

        // and an empty lobby
        deleteLobbies()

        // information to create a user
        val username = newTestUserName()
        val createUserResponse = createUserAndGetId(client, username) // userId is not needed

        // and the user already started a matchmaking process
        val lobby = startMatchmakingProcess(client, createUserResponse.token).getProperties()

        // when, then: the user tries to leave the matchmaking process is successful
        client.post().uri("/lobby/${lobby.path("id").asInt() + 1}/leave")
            .header("Authorization", "bearer ${createUserResponse.token}")
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .returnResult()
            .responseBody!!
    }

    @Test
    fun `join existing Lobby`() {
        // given: an HTTP client
        val client = WebTestClient
            .bindToServer()
            .baseUrl("http://localhost:$port/api")
            .responseTimeout(Duration.ofMinutes(1))
            .build()

        // and an empty lobby
        deleteLobbies()

        // information to create a user
        val username = newTestUserName()
        val createUserResponse = createUserAndGetId(client, username) // userId is not needed

        // and the user already started a matchmaking process
        val lobby = startMatchmakingProcess(client, createUserResponse.token).getProperties()

        // and another user
        val anotherUsername = newTestUserName()
        val anotherCreateUserResponse = createUserAndGetId(client, anotherUsername) // userId is not needed

        // when, then: the user tries to enter the lobby becomes a game
        val gameId = joinLobby(client, lobby.path("id").asInt(), anotherCreateUserResponse.token).getProperties()

        val gameDetails = getGameDetails(client, gameId.path("id").asInt(), anotherCreateUserResponse.token).getProperties().toGameResponse()
        assertNotNull(gameDetails.id)
        assertTrue(gameDetails.moves.orderOfMoves.isEmpty())
        assertNull(gameDetails.gameOutcome)
        assertEquals(gameDetails.type, "ONGOING")
    }

    @Test
    fun `join non-existing lobby`() {
        // given: an HTTP client
        val client = WebTestClient
            .bindToServer()
            .baseUrl("http://localhost:$port/api")
            .responseTimeout(Duration.ofMinutes(1))
            .build()

        // and an empty lobby
        deleteLobbies()

        // information to create a user
        val username = newTestUserName()
        val createUserResponse = createUserAndGetId(client, username) // userId is not needed

        // when, then: the user tries to enter the lobby becomes a game
        client.post().uri("/lobby/${Int.MAX_VALUE}/join")
            .header("Authorization", "bearer ${createUserResponse.token}")
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .returnResult()
            .responseBody!!
    }

    @Test
    fun `join lobby with same user`() {
        // given: an HTTP client
        val client = WebTestClient
            .bindToServer()
            .baseUrl("http://localhost:$port/api")
            .responseTimeout(Duration.ofMinutes(1))
            .build()

        // and an empty lobby
        deleteLobbies()

        // information to create 2 user
        val username = newTestUserName()
        val createUserResponse = createUserAndGetId(client, username) // userId is not needed

        // and the user already started a matchmaking process
        val lobby = startMatchmakingProcess(client, createUserResponse.token).getProperties()

        // when, then: the user tries to leave the matchmaking process is successful
        client.post().uri("/lobby/${lobby.path("id").asInt()}/join")
            .header("Authorization", "bearer ${createUserResponse.token}")
            .exchange()
            .expectStatus().is4xxClientError
            .expectBody()
            .returnResult()
            .responseBody!!
    }

    @Test
    fun `get lobbies of a user`() {
        // given: an HTTP client
        val client = WebTestClient
            .bindToServer()
            .baseUrl("http://localhost:$port/api")
            .responseTimeout(Duration.ofMinutes(1))
            .build()

        // and an empty lobby
        deleteLobbies()

        // information to create a user
        val username = newTestUserName()
        val createUserResponse = createUserAndGetId(client, username) // userId is not needed

        val nrOfRules = getRules(client).getProperties().path("size").asInt()
        require(nrOfRules >= 1)

        // and the user already started a matchmaking process
        createLobby(client, 1, createUserResponse.token)

        // when, then: the user gets the lobbies he is in and there is only one
        val lobbiesNr = getLobbies(client, createUserResponse.token).getProperties().path("size").asInt()
        assertEquals(1, lobbiesNr)
    }

    @Test
    fun `create lobby`() {
        // given: an HTTP client
        val client = WebTestClient
            .bindToServer()
            .baseUrl("http://localhost:$port/api")
            .responseTimeout(Duration.ofMinutes(1))
            .build()

        // and an empty lobby
        deleteLobbies()

        // information to create a user
        val username = newTestUserName()
        val createUserResponse = createUserAndGetId(client, username) // userId is not needed

        // when, then: the user creates several lobbies

        val lobby = createLobby(client, 1, createUserResponse.token).getProperties()
        assertContains(Int.MIN_VALUE..Int.MAX_VALUE, lobby.path("id").asInt())
    }

    @Test
    fun `get lobby by id`() {
        // given: an HTTP client
        val client = WebTestClient
            .bindToServer()
            .baseUrl("http://localhost:$port/api")
            .responseTimeout(Duration.ofMinutes(5))
            .build()

        // and an empty lobby
        deleteLobbies()

        // information to create a user
        val username = newTestUserName()
        val createUserResponse = createUserAndGetId(client, username) // userId is not needed

        // and the user already started a matchmaking process
        val matchMake = startMatchmakingProcess(client, createUserResponse.token).getProperties()

        val getLobbyResponse = getLobbyById(client, matchMake.path("id").asInt(), createUserResponse.token).getProperties()

        // when, then: the user tries to enter the lobby becomes a game
        assertEquals(matchMake.path("id").asInt(), getLobbyResponse.path("id").asInt())
    }

    @Test
    fun `get lobby by non-existing id`() {
        // given: an HTTP client
        val client = WebTestClient
            .bindToServer()
            .baseUrl("http://localhost:$port/api")
            .responseTimeout(Duration.ofMinutes(5))
            .build()

        // and an empty lobby
        deleteLobbies()

        // information to create a user
        val username = newTestUserName()
        val createUserResponse = createUserAndGetId(client, username) // userId is not needed

        // when, then: the user tries to enter the lobby becomes a game
        client.get().uri("/lobby/${Int.MAX_VALUE}")
            .header("Authorization", "bearer ${createUserResponse.token}")
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .returnResult()
            .responseBody!!
    }

    companion object {
        private fun newTestUserName() = "User${abs(Random.nextLong())}"

        private fun ByteArray.getProperties(): JsonNode {
            val objectMapper = jacksonObjectMapper()
            val jsonNode = objectMapper.readTree(this)
            return jsonNode.path("properties")
        }
    }
}
