package gomoku.server.http.controllers.game

import gomoku.server.domain.user.AuthenticatedUser
import gomoku.server.http.URIs
import gomoku.server.http.controllers.media.Problem
import gomoku.server.services.errors.game.CurrentTurnPlayerError
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
     * @param authenticatedUser The authenticated user
     * @return The list of games
     */
    @GetMapping(URIs.Game.HUB)
    fun games(authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        val matches = gameService.getUserFinishedMatches(userId = authenticatedUser.user.uuid)
        return ResponseEntity.ok(matches)
    }

    /**
     * Gets the details of a game
     * @param id The id of the game
     * @param authenticatedUser The authenticated user
     * @return The details of the game
     */
    @GetMapping(URIs.Game.GET_BY_ID)
    fun gameDetails(@PathVariable id: Int, authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        val game = gameService.getGame(id)
        return if (game != null) {
            ResponseEntity.ok(game)
        } else {
            Problem.response(Problem.gameNotFound)
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
            return Problem.response(Problem.noRulesFound)
        }

        return ResponseEntity.ok(rules)
    }

    /**
     * Makes a move in the context of a game
     * @param id The id of the game
     * @param authenticatedUser The authenticated user
     * @param pos The position of the move
     * @return The result of the move
     */
    @PostMapping(URIs.Game.MAKE_PLAY)
    fun makePlay(@PathVariable id: Int, authenticatedUser: AuthenticatedUser, @RequestParam pos: Int): ResponseEntity<*> {
        val moveResult = gameService.makeMove(id, authenticatedUser.user.uuid, pos)
        return when (moveResult) {
            is Failure -> moveResult.value.resolveProblem().also { println(it) }
            is Success -> ResponseEntity.ok(moveResult.value)
        }
    }

    /**
     * Starts the matchmaking process for a game, either
     * by creating a new lobby or joining a match
     * @param rulesId The id of the rule
     * @param authenticatedUser The authenticated user
     * @return The result of the matchmaking process
     */
    @PostMapping(URIs.Game.MATCH_MAKE)
    fun startMatchmaking(@PathVariable rulesId: Int, authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        val matchmaker = gameService.startMatchmakingProcess(rulesId, authenticatedUser.user.uuid)
        return when (matchmaker) {
            is Failure -> matchmaker.value.resolveProblem()
            is Success -> ResponseEntity.ok(matchmaker.value)
        }
    }

    /**
     * Leaves a lobby
     * @param lobbyId The id of the lobby
     * @param authenticatedUser The authenticated user
     * @return The result of leaving the lobby
     */
    @PostMapping(URIs.Game.LEAVE_LOBBY)
    fun leaveLobby(@PathVariable lobbyId: Int, authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        val leftLobby = gameService.leaveLobby(authenticatedUser.user.uuid)
        return if (!leftLobby) {
            Problem.response(Problem.lobbyNotFound)
        } else {
            ResponseEntity.ok(leftLobby)
        }
    }

    /**
     * Gets the current turn player id
     * @param id The id of the match
     * @return The current turn player id
     */
    @GetMapping(URIs.Game.TURN)
    fun currentTurnPlayerId(@PathVariable id: Int): ResponseEntity<*> {
        val currentTurnPlayerId = gameService.getCurrentTurnPlayerId(id)
        return when (currentTurnPlayerId) {
            is Failure -> currentTurnPlayerId.value.resolveProblem()
            is Success -> ResponseEntity.ok(currentTurnPlayerId.value)
        }
    }

    /**
     * Translates the errors of a Make move action into a response
     * @receiver The error
     * @return The response
     */
    private fun MakeMoveError.resolveProblem(): ResponseEntity<*> =
        when (this) {
            MakeMoveError.GameNotFound -> Problem.response(Problem.gameNotFound)
            MakeMoveError.GameFinished -> Problem.response(Problem.gameAlreadyFinished)
            MakeMoveError.AlreadyOccupied -> Problem.response(Problem.positionOccupied)
            MakeMoveError.ImpossiblePosition -> Problem.response(Problem.impossiblePosition)
            MakeMoveError.InvalidTurn -> Problem.response(Problem.notYourTurn)
            MakeMoveError.InvalidMove -> Problem.response(Problem.invalidMove)
            MakeMoveError.MakeMoveFailed -> Problem.response(Problem.makeMoveFailed)
        }

    /**
     * Translates the errors of a Matchmaking action into a response
     * @receiver The error
     * @return The response
     */
    private fun MatchmakingError.resolveProblem(): ResponseEntity<*> =
        when (this) {
            MatchmakingError.SamePlayer -> Problem.response(Problem.samePlayer)
            MatchmakingError.LeaveLobbyFailed -> Problem.response(Problem.leaveLobbyFailed)
        }

    /**
     * Translates the errors of a Current turn player action into a response
     * @receiver The error
     * @return The response
     */
    private fun CurrentTurnPlayerError.resolveProblem(): ResponseEntity<*> =
        when (this) {
            CurrentTurnPlayerError.NoTurn -> Problem.response(Problem.samePlayer)
            CurrentTurnPlayerError.MatchNotFound -> Problem.response(Problem.gameNotFound)
        }
}
