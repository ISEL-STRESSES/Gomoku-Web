package gomoku.server.http

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import gomoku.server.http.model.CreateUserResponse
import gomoku.server.http.model.PlayerRuleStatsResponse
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import kotlin.test.assertNotNull

private const val sirenMediaType = "application/vnd.siren+json"

/**
 * Util function:
 *
 * Starts the matchmaking process for a game, either
 * by creating a new lobby or joining a game
 * @param client the web test client
 * @param token The token of the user
 * @return The result of the matchmaking process
 */
fun startMatchmakingProcess(client: WebTestClient, token: String) =
    client.post().uri("/game/start/2")
        .header("Authorization", "bearer $token")
        .exchange()
        .expectStatus().is2xxSuccessful
        .expectBody()
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
 * @return The result of the move
 */
fun makeMove(client: WebTestClient, gameId: Int, x: Int, y: Int, token: String, headerContentType: String = sirenMediaType): ByteArray =
    client.post().uri("/game/$gameId/play?x=$x&y=$y")
        .header("Authorization", "bearer $token")
        .exchange()
        .expectHeader().valueEquals("Content-Type", headerContentType)
        .expectBody()
        .returnResult()
        .responseBody ?: throw Exception("Response body should not be null")

/**
 * Util function:
 *
 * Creates a user and returns its id
 * @param client the web test client
 * @param username The username of the user
 * @return The id of the user
 */
fun createUserAndGetId(client: WebTestClient, username: String): CreateUserResponse {
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
fun getFinishedGames(client: WebTestClient, token: String): ByteArray =
    client.get().uri("/game/").header("Authorization", "bearer $token")
        .exchange()
        .expectStatus().isOk
        .expectBody()
        .returnResult()
        .responseBody!!

/**
 * Util function:
 *
 * Gets the available rules
 * @param client the web test client
 * @return The list of rules
 */
fun getRules(client: WebTestClient): ByteArray =
    client.get().uri("/game/rules")
        .exchange()
        .expectStatus().isOk
        .expectBody()
        .returnResult()
        .responseBody!!

/**
 * Util function:
 *
 * Leaves a lobby
 * @param client the web test client
 * @param lobbyId The id of the lobby
 * @param userToken The token of the user
 * @return True if the user left the lobby, false otherwise
 */
fun leaveLobby(client: WebTestClient, lobbyId: Int, userToken: String) =
    client.post().uri("/lobby/$lobbyId/leave")
        .header("Authorization", "bearer $userToken")
        .exchange()
        .expectStatus().isOk
        .expectBody<Unit>()
        .returnResult()
        .responseBody!!

/**
 * Util function:
 *
 * Gets the details of a game
 * @param client the web test client
 * @param gameId The id of the game
 * @param token The token of the user
 * @return The details of the game
 */
fun getGameDetails(client: WebTestClient, gameId: Int, token: String) =
    client.get().uri("/game/$gameId")
        .header("Authorization", "bearer $token")
        .exchange()
        .expectStatus().isOk
        .expectBody()
        .returnResult()
        .responseBody!!

/**
 * Util function:
 *
 * Gets the current turn player id
 * @param client the web test client
 * @param gameId The id of the game
 * @return The current turn player id
 */
fun getTurnFromGame(client: WebTestClient, gameId: Int, userToken: String) =
    client.get().uri("/game/$gameId/turn")
        .header("Authorization", "bearer $userToken")
        .exchange()
        .expectStatus().isOk
        .expectBody()
        .returnResult()
        .responseBody!!

fun getPlayerRuleStats(client: WebTestClient, userId: Int): PlayerRuleStatsResponse {
    var playerRuleStatsResponse: PlayerRuleStatsResponse? = null
    client.get().uri("/users/$userId/ranking/2")
        .exchange()
        .expectStatus().isOk
        .expectBody()
        .consumeWith { response ->
            val json = String(response.responseBody!!)
            val node = ObjectMapper().readTree(json)
            val properties = node.get("properties")
            val ruleIdR = properties.get("ruleId").asInt()
            val userIdR = properties.get("id").asInt()
            val username = properties.get("username").asText()
            val gamesPlayed = properties.get("gamesPlayed").asInt()
            val elo = properties.get("elo").asInt()
            assertNotNull(userIdR)
            assertNotNull(username)
            assertNotNull(ruleIdR)
            assertNotNull(gamesPlayed)
            assertNotNull(elo)
            playerRuleStatsResponse = PlayerRuleStatsResponse(userIdR, username, ruleIdR, gamesPlayed, elo)
        }
    requireNotNull(playerRuleStatsResponse)
    return playerRuleStatsResponse!!
}

fun forfeitGame(client: WebTestClient, gameId: Int, userToken: String): ByteArray =
    client.post().uri("/game/$gameId/forfeit")
        .header("Authorization", "bearer $userToken")
        .exchange()
        .expectStatus().isOk
        .expectBody()
        .returnResult()
        .responseBody!!

fun joinLobby(client: WebTestClient, lobbyId: Int, userToken: String) =
    client.post().uri("/lobby/$lobbyId/join")
        .header("Authorization", "bearer $userToken")
        .exchange()
        .expectStatus().isCreated
        .expectBody()
        .returnResult()
        .responseBody!!

fun getLobbies(client: WebTestClient, userToken: String): ByteArray =
    client.get().uri("/lobbies")
        .header("Authorization", "bearer $userToken")
        .exchange()
        .expectStatus().isOk
        .expectBody()
        .returnResult()
        .responseBody!!

fun createLobby(client: WebTestClient, ruleId: Int, userToken: String): ByteArray =
    client.post().uri("/lobby/create/$ruleId")
        .header("Authorization", "bearer $userToken")
        .exchange()
        .expectStatus().isCreated
        .expectBody()
        .returnResult()
        .responseBody!!

fun getLobbyById(client: WebTestClient, lobbyId: Int, userToken: String): ByteArray =
    client.get().uri("/lobby/$lobbyId")
        .header("Authorization", "bearer $userToken")
        .exchange()
        .expectStatus().isOk
        .expectBody()
        .returnResult()
        .responseBody!!
