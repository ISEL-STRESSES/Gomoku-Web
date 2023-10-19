package gomoku.server.http.controllers.game

import gomoku.server.domain.user.AuthenticatedUser
import gomoku.server.http.URIs
import gomoku.server.http.controllers.media.Problem
import gomoku.server.services.errors.game.MakeMoveError
import gomoku.server.services.errors.game.MatchmakingError
import gomoku.server.services.game.GameService
import gomoku.utils.Failure
import gomoku.utils.Success
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Controller for game-related endpoints
 * @property gameService The game service
 */
@RestController(value = "Games")
@RequestMapping(URIs.Game.ROOT)
class GameController(private val gameService: GameService) {

    /**
     * Gets the list of games
     * @param offset The offset
     * @param limit The limit
     * @param authenticatedUser The authenticated user
     * @return The list of games
     */
    @GetMapping(URIs.Game.HUB)
    fun games(
        @RequestParam offset: Int = 0,
        @RequestParam limit: Int = 10,
        @RequestParam authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val matches = gameService.getUserFinishedMatches(offset, limit, authenticatedUser.user.uuid)
        return ResponseEntity.ok(matches)
    }

    /**
     * Gets the details of a game
     * @param id The id of the game
     * @param authenticatedUser The authenticated user
     * @return The details of the game
     */
    @GetMapping(URIs.Game.GET_BY_ID)
    fun gameDetails(@PathVariable id: Int, @RequestParam authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        val game = gameService.getGame(id)
        return if (game != null) {
            ResponseEntity.ok(game)
        } else {
            Problem.response(404, Problem.gameNotFound)
        }
    }

    /**
     * Gets the available rules to play the game
     * @return The available rules
     */
    @GetMapping(URIs.Game.GAME_RULES)
    fun rules(): ResponseEntity<*> {
        val rules = gameService.getAvailableRules()
        if (rules.isEmpty()) {
            return Problem.response(404, Problem.noRulesFound)
        }

        return ResponseEntity.ok(rules)
    }

    /**
     * Makes a move in the context of a game
     * @param id The id of the game
     * @param userId The id of the user
     * @param pos The position of the move
     * @return The result of the move
     */
    @PostMapping(URIs.Game.MAKE_PLAY)
    fun makePlay(@PathVariable id: Int, @RequestParam userId: Int, @RequestParam pos: Int): ResponseEntity<*> {
        val moveResult = gameService.makeMove(id, userId, pos)
        return when (moveResult) {
            is Failure -> moveResult.value.resolveProblem()
            is Success -> ResponseEntity.ok(moveResult)
        }
    }

    /**
     * Starts the matchmaking process for a game, either
     * by creating a new lobby or joining a match
     * @param rulesId The id of the rule
     * @param userId The id of the user
     * @return The result of the matchmaking process
     */
    @PostMapping(URIs.Game.MATCH_MAKE)
    fun startMatchmaking(@PathVariable rulesId: Int, @RequestParam userId: Int): ResponseEntity<*> {
        val matchmaker = gameService.startMatchmakingProcess(rulesId, userId)
        return when (matchmaker) {
            is Failure -> matchmaker.value.resolveProblem()
            is Success -> ResponseEntity.ok(matchmaker)
        }
    }

    /**
     * Leaves a lobby
     * @param lobbyId The id of the lobby
     * @param authenticatedUser The authenticated user
     * @return The result of leaving the lobby
     */
    @PostMapping(URIs.Game.LEAVE_LOBBY)
    fun leaveLobby(@PathVariable lobbyId: Int, @RequestParam authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        val leftLobby = gameService.leaveLobby(authenticatedUser.user.uuid)
        return if (!leftLobby) {
            Problem.response(404, Problem.lobbyNotFound)
        } else {
            ResponseEntity.ok(leftLobby)
        }
    }

    /**
     * Gets the current turn player id
     * @param id The id of the match
     * @return The current turn player id
     */
    @GetMapping(URIs.Game.JOIN)
    fun currentTurnPlayerId(@PathVariable id: Int): ResponseEntity<*> {
        val currentTurnPlayerId = gameService.getCurrentTurnPlayerId(id)
        return when (currentTurnPlayerId) {
            null -> Problem.response(404, Problem.gameNotFound)
            else -> ResponseEntity.ok(currentTurnPlayerId)
        }
    }

    /**
     * Translates the errors of a Make move action into a response
     * @receiver The error
     * @return The response
     */
    private fun MakeMoveError.resolveProblem(): ResponseEntity<*> =
        when (this) {
            MakeMoveError.GameNotFound -> Problem.response(404, Problem.gameNotFound)
            MakeMoveError.GameFinished -> Problem.response(400, Problem.gameAlreadyFinished)
            MakeMoveError.AlreadyOccupied -> Problem.response(400, Problem.positionOccupied)
            MakeMoveError.ImpossiblePosition -> Problem.response(400, Problem.impossiblePosition)
            MakeMoveError.InvalidTurn -> Problem.response(400, Problem.notYourTurn)
            MakeMoveError.MakeMoveFailed -> Problem.response(500, Problem.makeMoveFailed)
        }

    /**
     * Translates the errors of a Matchmaking action into a response
     * @receiver The error
     * @return The response
     */
    private fun MatchmakingError.resolveProblem(): ResponseEntity<*> =
        when (this) {
            MatchmakingError.SamePlayer -> Problem.response(400, Problem.samePlayer)
            MatchmakingError.LeaveLobbyFailed -> Problem.response(500, Problem.leaveLobbyFailed)
        }

    // TODO: MAKE THE OTHER RESOLVE PROBLEM FUNCTIONS
    // TODO: Make all user functions return a Result and resolve error on controller layer?????
}
