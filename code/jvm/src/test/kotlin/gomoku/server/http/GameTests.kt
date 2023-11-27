package gomoku.server.http

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import gomoku.server.deleteLobbies
import gomoku.server.http.controllers.game.models.GameOutputModel
import gomoku.server.http.model.toFinishedGamesResponse
import gomoku.server.http.model.toGameResponse
import gomoku.server.http.model.toRulesResponse
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.Duration
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GameTests {
    // One of the very few places where we use property injection
    @LocalServerPort
    var port: Int = 8080

    private val sirenMediaType = "application/vnd.siren+json"
    private val problemJsonMediaType = "application/problem+json"

    @Test
    fun `get available rules`() {
        // given: an HTTP client
        val client = WebTestClient
            .bindToServer()
            .baseUrl("http://localhost:$port/api")
            .responseTimeout(Duration.ofHours(1))
            .build()

        // when: the user tries to get the available rules
        val rules = getRules(client).parseJson().toRulesResponse()

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
        val finishedGames = getFinishedGames(client, createUserResponse.token).parseJson().toFinishedGamesResponse()

        // then: assert that a user doesn't have any finished games
        assertTrue(finishedGames.gameList.isEmpty())
    }

    @Test
    fun `get game details`() {
        // given: an HTTP client
        val client = WebTestClient
            .bindToServer()
            .baseUrl("http://localhost:$port/api")
            .responseTimeout(Duration.ofHours(1))
            .build()

        // and an empty lobbies
        deleteLobbies()

        // and information to create 2 users
        val username1 = newTestUserName()
        val createUserResponse = createUserAndGetId(client, username1)

        val username2 = newTestUserName()
        val createUserResponse2 = createUserAndGetId(client, username2)

        // and a game
        startMatchmakingProcess(client, createUserResponse.token)
        val game = startMatchmakingProcess(client, createUserResponse2.token).parseJson()

        // when: the user tries to get the game details
        val gameDetails = getGameDetails(client, game.path("id").asInt(), createUserResponse.token).parseJson()
        val gameResponse = gameDetails.toGameResponse()
        assertEquals(gameResponse.type, GameOutputModel.GameType.ONGOING.name)

        // then: assert that the game details are correct
        assertNotEquals(gameResponse.playerWhite, gameResponse.playerBlack)
        assertTrue(gameResponse.moves.orderOfMoves.isEmpty())
        assertNull(gameResponse.gameOutcome)
    }

    @Test
    fun `get game details from a non existing game`() {
        // given: an HTTP client
        val client = WebTestClient
            .bindToServer()
            .baseUrl("http://localhost:$port/api")
            .responseTimeout(Duration.ofHours(1))
            .build()

        // and an empty lobbies
        deleteLobbies()

        // and information to create a user
        val username = newTestUserName()
        val createUserResponse = createUserAndGetId(client, username)

        // when: the user tries to get the game details from a non existing game
        // then: assert that the game details are not found
        client.get().uri("/game/${Int.MAX_VALUE}")
            .header("Authorization", "bearer ${createUserResponse.token}")
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .returnResult()
            .responseBody!!
    }

    @Test
    fun `get game details from a game that the player is not in`() {
        // given: an HTTP client
        val client = WebTestClient
            .bindToServer()
            .baseUrl("http://localhost:$port/api")
            .responseTimeout(Duration.ofHours(1))
            .build()

        // and an empty lobbies
        deleteLobbies()

        // and information to create 2 users
        val username1 = newTestUserName()
        val createUserResponse1 = createUserAndGetId(client, username1)

        val username2 = newTestUserName()
        val createUserResponse2 = createUserAndGetId(client, username2)

        val username3 = newTestUserName()
        val createUserResponse3 = createUserAndGetId(client, username3)

        // and a game
        startMatchmakingProcess(client, createUserResponse1.token)
        val game = startMatchmakingProcess(client, createUserResponse2.token).parseJson()

        // when: the user tries to get the game details from a game that the player is not in
        // then: assert that the game details are not found
        client.get().uri("/game/${game.path("id").asInt()}")
            .header("Authorization", "bearer ${createUserResponse3.token}")
            .exchange()
            .expectStatus().isUnauthorized
            .expectBody()
            .returnResult()
            .responseBody!!
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

        // and a game
        startMatchmakingProcess(client, createUserResponse.token)
        val game = startMatchmakingProcess(client, createUserResponse2.token).parseJson()

        // and a turn
        val getTurn = getTurnFromGame(client, game.path("id").asInt(), createUserResponse.token).parseJson()
        val currentPlayer =
            if (getTurn.path("turn").asInt() == createUserResponse.userId) createUserResponse.token else createUserResponse2.token

        // when: the user tries to make a valid move
        val gameAfterValidMove = makeMove(client, game.path("id").asInt(), 0, 0, currentPlayer).parseJson().toGameResponse()

        // then: assert that the move is valid
        assertTrue(gameAfterValidMove.moves.orderOfMoves.size == 1)
        assertNull(gameAfterValidMove.gameOutcome)
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

        // and a game
        startMatchmakingProcess(client, createUserResponse.token)
        val gameId = startMatchmakingProcess(client, createUserResponse2.token).parseJson().path("id").asInt()

        val turn = getTurnFromGame(client, gameId, createUserResponse.token).parseJson().path("turn").asInt()

        val playerBlack = if (turn == createUserResponse.userId) createUserResponse.token else createUserResponse2.token
        val playerWhite = if (turn == createUserResponse.userId) createUserResponse2.token else createUserResponse.token

        // and a valid finished game
        makeMove(client, gameId, 0, 0, playerBlack)
        makeMove(client, gameId, 9, 9, playerWhite)
        makeMove(client, gameId, 0, 1, playerBlack)
        makeMove(client, gameId, 11, 11, playerWhite)
        makeMove(client, gameId, 0, 2, playerBlack)
        makeMove(client, gameId, 11, 12, playerWhite)
        makeMove(client, gameId, 0, 3, playerBlack)
        makeMove(client, gameId, 13, 13, playerWhite)
        val finishedGame = makeMove(client, gameId, 0, 4, playerBlack).parseJson().toGameResponse() // winning move
        assertEquals("BLACK_WON", finishedGame.gameOutcome)
        assertNull(finishedGame.turn)

        // when: the user tries to make a move when the game is finished
        // then: assert that the move is invalid
        makeMove(client, gameId, 14, 14, playerWhite, problemJsonMediaType)
        makeMove(client, gameId, 8, 10, playerBlack, problemJsonMediaType)
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

        // and a game
        startMatchmakingProcess(client, createUserResponse.token)
        val game = startMatchmakingProcess(client, createUserResponse2.token).parseJson()

        // and a turn
        val getTurn = getTurnFromGame(client, game.path("id").asInt(), createUserResponse.token).parseJson()
        val currentPlayer =
            if (getTurn.path("turn").asInt() == createUserResponse.userId) createUserResponse.token else createUserResponse2.token

        // when: the user tries to make a move out of bounds
        // then: assert that the move is out of bounds
        makeMove(client, game.path("id").asInt(), Int.MAX_VALUE, Int.MAX_VALUE, currentPlayer, problemJsonMediaType)
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

        // and a game
        startMatchmakingProcess(client, createUserResponse.token)
        val game = startMatchmakingProcess(client, createUserResponse2.token).parseJson()

        // and a turn
        val getTurn = getTurnFromGame(client, game.path("id").asInt(), createUserResponse.token).parseJson()
        val otherPlayer =
            if (getTurn.path("turn").asInt() == createUserResponse.userId) createUserResponse2.token else createUserResponse.token

        // when: the user tries to make a move when it's not his turn
        // then: assert that the move is invalid
        makeMove(client, game.path("id").asInt(), 0, 0, otherPlayer, problemJsonMediaType)
    }

    @Test
    fun `make move when your not in game`() {
        // given: an HTTP client
        val client = WebTestClient
            .bindToServer()
            .baseUrl("http://localhost:$port/api")
            .responseTimeout(Duration.ofHours(1))
            .build()

        // and an empty lobby
        deleteLobbies()

        // and information to create 3 users
        val username1 = newTestUserName()
        val createUserResponse = createUserAndGetId(client, username1)

        val username2 = newTestUserName()
        val createUserResponse2 = createUserAndGetId(client, username2)

        val username3 = newTestUserName()
        val createUserResponse3 = createUserAndGetId(client, username3)

        // and a game
        startMatchmakingProcess(client, createUserResponse.token)
        val game = startMatchmakingProcess(client, createUserResponse2.token).parseJson()

        // when: the user tries to make a move when it's not his game
        // then: assert that the move is invalid
        makeMove(client, game.path("id").asInt(), 0, 0, createUserResponse3.token, problemJsonMediaType)
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
        makeMove(client, Int.MAX_VALUE, 0, 0, createUserResponse.token, problemJsonMediaType)
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

        // and a game
        startMatchmakingProcess(client, createUserResponse.token)
        val game = startMatchmakingProcess(client, createUserResponse2.token).parseJson()

        // and a turn
        val getTurn = getTurnFromGame(client, game.path("id").asInt(), createUserResponse.token).parseJson()
        val currentPlayer =
            if (getTurn.path("turn").asInt() == createUserResponse.userId) createUserResponse.token else createUserResponse2.token
        val otherPlayer =
            if (getTurn.path("turn").asInt() == createUserResponse.userId) createUserResponse2.token else createUserResponse.token

        // and a valid move
        makeMove(client, game.path("id").asInt(), 0, 0, currentPlayer)

        // when: the user tries to make an already occupied move
        // then: assert that the move is already occupied
        makeMove(client, game.path("id").asInt(), 0, 0, otherPlayer, problemJsonMediaType)
    }

    @Test
    fun `start matchmaking process`() {
        // given: an HTTP client
        val client = WebTestClient
            .bindToServer()
            .baseUrl("http://localhost:$port/api")
            .responseTimeout(Duration.ofHours(1))
            .build()

        // information to create a user
        val username = newTestUserName()
        val createUserResponse = createUserAndGetId(client, username) // userId is not needed

        // and an empty lobby
        deleteLobbies()

        // when: the user tries to start a matchmaking process
        val lobby = startMatchmakingProcess(client, createUserResponse.token).parseJson()

        // then: assert that the user is in a lobby
        assertFalse(lobby.path("isGame").asBoolean())
    }

    @Test
    fun `start matchmaking process twice`() {
        // given: an HTTP client
        val client = WebTestClient
            .bindToServer()
            .baseUrl("http://localhost:$port/api")
            .responseTimeout(Duration.ofHours(1))
            .build()

        // information to create a user
        val username = newTestUserName()
        val createUserResponse = createUserAndGetId(client, username) // userId is not needed

        // and an empty lobby
        deleteLobbies()

        // when: the user tries to start a matchmaking process
        startMatchmakingProcess(client, createUserResponse.token)

        client.post().uri("/game/start/2")
            .header("Authorization", "bearer ${createUserResponse.token}")
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .returnResult()
            .responseBody!!
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

        // information to create a user
        val username = newTestUserName()
        val createUserResponse = createUserAndGetId(client, username) // userId is not needed

        // and the user already started a matchmaking process
        startMatchmakingProcess(client, createUserResponse.token) // lobbyId is not needed

        // when, then: the user tries to start a matchmaking process again should return a bad request
        client.post().uri("/game/start/2")
            .header("Authorization", "bearer ${createUserResponse.token}")
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .returnResult()
            .responseBody!!
    }

    @Test
    fun `start matchmaking process results in limbo`() {
        // TODO("To test this maybe in a case of race condition, but it's hard to test")
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

        // and a game
        startMatchmakingProcess(client, createUserResponse.token)
        val game = startMatchmakingProcess(client, createUserResponse2.token).parseJson()

        // when: the user tries to get the current turn player id
        val currentTurnPlayerId = getTurnFromGame(client, game.path("id").asInt(), createUserResponse.token).parseJson().path("turn").asInt()

        // then: assert that the current turn player id is on the game
        assertTrue((currentTurnPlayerId == createUserResponse.userId) or (currentTurnPlayerId == createUserResponse2.userId))

        // and the turn is for player black
        assertEquals(
            currentTurnPlayerId,
            getGameDetails(client, game.path("id").asInt(), createUserResponse.token).parseJson().toGameResponse().playerBlack
        )
    }

    @Test
    fun `get turn from finished game`() {
        // given: an HTTP client
        val client = WebTestClient
            .bindToServer()
            .baseUrl("http://localhost:$port/api")
            .responseTimeout(Duration.ofMinutes(1))
            .build()

        // and an empty lobby
        deleteLobbies()

        // and 2 authenticated users
        val username1 = newTestUserName()
        val createUserResponse = createUserAndGetId(client, username1)

        val username2 = newTestUserName()
        val createUserResponse2 = createUserAndGetId(client, username2)

        // and a game
        startMatchmakingProcess(client, createUserResponse.token)
        val gameId = startMatchmakingProcess(client, createUserResponse2.token).parseJson().path("id").asInt()

        val turn = getTurnFromGame(client, gameId, createUserResponse.token).parseJson().path("turn").asInt()

        val playerBlack = if (turn == createUserResponse.userId) createUserResponse.token else createUserResponse2.token
        val playerWhite = if (turn == createUserResponse.userId) createUserResponse2.token else createUserResponse.token

        // and a valid finished game
        makeMove(client, gameId, 0, 0, playerBlack)
        makeMove(client, gameId, 9, 9, playerWhite)
        makeMove(client, gameId, 0, 1, playerBlack)
        makeMove(client, gameId, 11, 11, playerWhite)
        makeMove(client, gameId, 0, 2, playerBlack)
        makeMove(client, gameId, 11, 12, playerWhite)
        makeMove(client, gameId, 0, 3, playerBlack)
        makeMove(client, gameId, 13, 13, playerWhite)
        val finishedGame = makeMove(client, gameId, 0, 4, playerBlack).parseJson().toGameResponse() // winning move
        assertEquals("BLACK_WON", finishedGame.gameOutcome)
        assertNull(finishedGame.turn)

        // when: the user tries to get the current turn player id from a finished game
        // then: assert that the current turn player id is null
        client.get().uri("/game/$gameId/turn")
            .header("Authorization", "bearer ${createUserResponse.token}")
            .exchange()
            .expectStatus().isBadRequest
            .expectBody()
            .returnResult()
            .responseBody!!
    }

    @Test
    fun `get turn from a game that the user is not`() {
        // given: an HTTP client
        val client = WebTestClient
            .bindToServer()
            .baseUrl("http://localhost:$port/api")
            .responseTimeout(Duration.ofMinutes(1))
            .build()

        // and an empty lobby
        deleteLobbies()

        // and information to create 3 users
        val username1 = newTestUserName()
        val createUserResponse = createUserAndGetId(client, username1)

        val username2 = newTestUserName()
        val createUserResponse2 = createUserAndGetId(client, username2)

        val username3 = newTestUserName()
        val createUserResponse3 = createUserAndGetId(client, username3)

        // and a game
        startMatchmakingProcess(client, createUserResponse.token)
        val game = startMatchmakingProcess(client, createUserResponse2.token).parseJson()

        // when: the user tries to get the current turn player id from a game that the user is not
        // then: assert that the current turn player id is null
        client.get().uri("/game/${game.path("id").asInt()}/turn")
            .header("Authorization", "bearer ${createUserResponse3.token}")
            .exchange()
            .expectStatus().isUnauthorized
            .expectBody()
            .returnResult()
            .responseBody!!
    }

    @Test
    fun `get turn from a non existing game`() {
        // given: an HTTP client
        val client = WebTestClient
            .bindToServer()
            .baseUrl("http://localhost:$port/api")
            .responseTimeout(Duration.ofMinutes(1))
            .build()

        // and an empty lobby
        deleteLobbies()

        // and information to create a user
        val username = newTestUserName()
        val createUserResponse = createUserAndGetId(client, username)

        // when: the user tries to get the current turn player id from a non existing game
        // then: assert that the current turn player id is null
        client.get().uri("/game/${Int.MAX_VALUE}/turn")
            .header("Authorization", "bearer ${createUserResponse.token}")
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .returnResult()
            .responseBody!!
    }

    @Test
    fun `forfeit game`() {
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

        // and a game
        startMatchmakingProcess(client, createUserResponse.token)
        val gameId = startMatchmakingProcess(client, createUserResponse2.token).parseJson().path("id").asInt()

        println(getGameDetails(client, gameId, createUserResponse.token))

        // when: the user tries to forfeit the game
        val forfeitGame = forfeitGame(client, gameId, createUserResponse.token).parseJson().toGameResponse()

        // then: assert that the game is forfeited
        assertContains(arrayOf("BLACK_WON", "WHITE_WON"), forfeitGame.gameOutcome)
        assertNull(forfeitGame.turn)
    }

    @Test
    fun `forfeit form a non existing game`() {
        // given: an HTTP client
        val client = WebTestClient
            .bindToServer()
            .baseUrl("http://localhost:$port/api")
            .responseTimeout(Duration.ofMinutes(1))
            .build()

        // and an empty lobby
        deleteLobbies()

        // and information to create a user
        val username = newTestUserName()
        val createUserResponse = createUserAndGetId(client, username)

        // when: the user tries to forfeit a non existing game
        // then: assert that the game is not found
        client.post().uri("/game/${Int.MAX_VALUE}/forfeit")
            .header("Authorization", "bearer ${createUserResponse.token}")
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .returnResult()
            .responseBody!!
    }

    @Test
    fun `forfeit from a game that the user is not in`() {
        // given: an HTTP client
        val client = WebTestClient
            .bindToServer()
            .baseUrl("http://localhost:$port/api")
            .responseTimeout(Duration.ofMinutes(1))
            .build()

        // and an empty lobby
        deleteLobbies()

        // and information to create 3 users
        val username1 = newTestUserName()
        val createUserResponse = createUserAndGetId(client, username1)

        val username2 = newTestUserName()
        val createUserResponse2 = createUserAndGetId(client, username2)

        val username3 = newTestUserName()
        val createUserResponse3 = createUserAndGetId(client, username3)

        // and a game
        startMatchmakingProcess(client, createUserResponse.token)
        val game = startMatchmakingProcess(client, createUserResponse2.token).parseJson()

        // when: the user tries to forfeit a game that the user is not in
        // then: assert that the game is not found
        client.post().uri("/game/${game.path("id").asInt()}/forfeit")
            .header("Authorization", "bearer ${createUserResponse3.token}")
            .exchange()
            .expectStatus().isUnauthorized
            .expectBody()
            .returnResult()
            .responseBody!!
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

        // and a game
        startMatchmakingProcess(client, createUserResponse.token)
        val game = startMatchmakingProcess(client, createUserResponse2.token).parseJson()
        assertTrue(game.path("isGame").asBoolean())
        val gameID = game.path("id").asInt()
        // and a turn
        val playerBlackId = getTurnFromGame(client, gameID, createUserResponse.token).parseJson().path("turn").asInt()

        // and player Black and player White
        val getPlayerBlack =
            if (playerBlackId == createUserResponse.userId) createUserResponse.token else createUserResponse2.token
        val getPlayerWhite =
            if (playerBlackId == createUserResponse.userId) createUserResponse2.token else createUserResponse.token

        // when: the user tries to make a move when it's not his turn
        // then: assert that the move is invalid
        makeMove(client, gameID, 9, 9, getPlayerWhite, problemJsonMediaType)

        // when: the user tries to make a valid move
        val validMove1 = makeMove(client, gameID, 0, 0, getPlayerBlack, sirenMediaType).parseJson().toGameResponse()
        // then: assert that the move is valid
        assertTrue(validMove1.moves.orderOfMoves.size == 1)
        assertNull(validMove1.gameOutcome)

        // when: the user tries to make a valid move
        val validMove2 = makeMove(client, gameID, 2, 10, getPlayerWhite, sirenMediaType).parseJson().toGameResponse()
        // then: assert that the move is valid
        assertTrue(validMove2.moves.orderOfMoves.size == 2)
        assertNull(validMove1.gameOutcome)

        // when: the users play valid moves till 'almost' the end of the game
        makeMove(client, gameID, 0, 1, getPlayerBlack)
        makeMove(client, gameID, 11, 11, getPlayerWhite)
        makeMove(client, gameID, 0, 2, getPlayerBlack)
        makeMove(client, gameID, 11, 12, getPlayerWhite)
        makeMove(client, gameID, 0, 3, getPlayerBlack)
        makeMove(client, gameID, 9, 9, getPlayerWhite)

        // when: the user tries to make a valid winning move
        val gameAfterFinishingMove = makeMove(client, gameID, 0, 4, getPlayerBlack).parseJson().toGameResponse()

        // then: assert that was a winning move and the winner is the player black
        assertEquals("BLACK_WON", gameAfterFinishingMove.gameOutcome)
        assertEquals(playerBlackId, gameAfterFinishingMove.playerBlack)
        assertNull(gameAfterFinishingMove.turn)

        // and assert that the stats of the players are updated
        val playerBlackStats = getPlayerRuleStats(client, playerBlackId)
        val playerWhiteId =
            if (playerBlackId == createUserResponse.userId) createUserResponse2.userId else createUserResponse.userId
        val playerWhiteStats = getPlayerRuleStats(client, playerWhiteId)
        assertEquals(1, playerBlackStats.gamesPlayed)
        assertEquals(1, playerWhiteStats.gamesPlayed)
        assertTrue { playerBlackStats.elo > playerWhiteStats.elo }
    }

    companion object {
        private fun newTestUserName() = "User${abs(Random.nextLong())}"

        private fun ByteArray.parseJson(): JsonNode {
            val objectMapper = jacksonObjectMapper()
            val jsonNode = objectMapper.readTree(this)
            return jsonNode.path("properties")
        }
    }
}
