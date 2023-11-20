package gomoku.server.http.controllers.game

import gomoku.server.domain.user.AuthenticatedUser
import gomoku.server.http.URIs
import gomoku.server.http.controllers.game.models.GameOutputModel
import gomoku.server.http.controllers.game.models.GetFinishedGamesOutputModel
import gomoku.server.http.controllers.game.models.GetRulesOutputModel
import gomoku.server.http.controllers.game.models.MatchmakerOutputModel
import gomoku.server.http.controllers.game.models.RuleOutputModel
import gomoku.server.http.controllers.media.Problem
import gomoku.server.http.responses.GetFinishedGames
import gomoku.server.http.responses.GetGameById
import gomoku.server.http.responses.GetRules
import gomoku.server.http.responses.GetTurn
import gomoku.server.http.responses.LeaveLobby
import gomoku.server.http.responses.MakeMove
import gomoku.server.http.responses.Matchmaker
import gomoku.server.http.responses.response
import gomoku.server.services.errors.game.CurrentTurnPlayerError
import gomoku.server.services.errors.game.GetGameError
import gomoku.server.services.errors.game.LeaveLobbyError
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
    fun finishedGames(authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        val games = gameService.getUserFinishedGames(userId = authenticatedUser.user.uuid)
        return GetFinishedGames.siren(GetFinishedGamesOutputModel(games.map { GameOutputModel.fromGame(it) }))
            .response(200)
    }

    /**
     * Gets the details of a game
     * @param id The id of the game
     * @param authenticatedUser The authenticated user
     * @return The details of the game
     */
    @GetMapping(URIs.Game.GET_BY_ID)
    fun gameDetails(@PathVariable id: Int, authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        val game = gameService.getGame(id, authenticatedUser.user.uuid)
        return when (game) {
            is Failure -> game.value.resolveProblem()
            is Success -> GetGameById.siren(GameOutputModel.fromGame(game.value)).response(200)
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

        return GetRules.siren(GetRulesOutputModel(rules.map { RuleOutputModel(it) })).response(200)
    }

    /**
     * Makes a move in the context of a game
     * @param id The id of the game
     * @param authenticatedUser The authenticated user
     * @param x The x coordinate of the move
     * @param y The y coordinate of the move
     * @return The result of the move
     */
    @PostMapping(URIs.Game.MAKE_PLAY)
    fun makePlay(
        @PathVariable id: Int,
        authenticatedUser: AuthenticatedUser,
        @RequestParam x: Int,
        @RequestParam y: Int
    ): ResponseEntity<*> {
        val moveResult = gameService.makeMove(id, authenticatedUser.user.uuid, x, y)
        return when (moveResult) {
            is Failure -> moveResult.value.resolveProblem()
            is Success ->
                MakeMove.siren(GameOutputModel.fromGame(moveResult.value)).response(200)
        }
    }

    /**
     * Starts the matchmaking process for a game, either
     * by creating a new lobby or joining a game
     * @param rulesId The id of the rule
     * @param authenticatedUser The authenticated user
     * @return The result of the matchmaking process
     */
    @PostMapping(URIs.Game.MATCH_MAKE)
    fun startMatchmaking(@PathVariable rulesId: Int, authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        val matchmaker = gameService.startMatchmakingProcess(rulesId, authenticatedUser.user.uuid)
        return when (matchmaker) {
            is Failure -> matchmaker.value.resolveProblem()
            is Success -> Matchmaker.siren(MatchmakerOutputModel(matchmaker.value)).response(201)
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
        val leftLobby = gameService.leaveLobby(lobbyId, authenticatedUser.user.uuid)
        return when (leftLobby) {
            is Failure -> leftLobby.value.resolveProblem()
            is Success -> LeaveLobby.siren(leftLobby.value).response(200)
        }
    }

    /**
     * Gets the current turn player id
     * @param id The id of the game
     * @return The current turn player id
     */
    @GetMapping(URIs.Game.TURN)
    fun currentTurnPlayerId(@PathVariable id: Int): ResponseEntity<*> {
        val currentTurnPlayerId = gameService.getCurrentTurnPlayerId(id)
        return when (currentTurnPlayerId) {
            is Failure -> currentTurnPlayerId.value.resolveProblem()
            is Success -> GetTurn.siren(currentTurnPlayerId.value).response(200)
        }
    }

    /**
     * Translates the errors of a Make move action into a response
     * @receiver The error
     * @return The response
     */
    private fun MakeMoveError.resolveProblem(): ResponseEntity<*> =
        when (this) {
            MakeMoveError.GameFinished -> Problem.response(400, Problem.gameAlreadyFinished)
            MakeMoveError.ImpossiblePosition -> Problem.response(400, Problem.impossiblePosition)
            MakeMoveError.InvalidTurn -> Problem.response(400, Problem.notYourTurn)
            MakeMoveError.InvalidMove -> Problem.response(400, Problem.invalidMove)
            MakeMoveError.PlayerNotInGame -> Problem.response(401, Problem.playerNotInGame)
            MakeMoveError.GameNotFound -> Problem.response(404, Problem.gameNotFound)
            MakeMoveError.PlayerNotFound -> Problem.response(404, Problem.userNotFound)
            MakeMoveError.AlreadyOccupied -> Problem.response(409, Problem.positionOccupied)
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

    /**
     * Translates the errors of a Current turn player action into a response
     * @receiver The error
     * @return The response
     */
    private fun CurrentTurnPlayerError.resolveProblem(): ResponseEntity<*> =
        when (this) {
            CurrentTurnPlayerError.GameAlreadyFinished -> Problem.response(400, Problem.gameAlreadyFinished)
            CurrentTurnPlayerError.GameNotFound -> Problem.response(404, Problem.gameNotFound)
        }

    /**
     * Translates the errors of a Get game action into a response
     * @receiver The error
     * @return The response
     */
    private fun GetGameError.resolveProblem(): ResponseEntity<*> =
        when (this) {
            GetGameError.PlayerNotInGame -> Problem.response(401, Problem.playerNotInGame)
            GetGameError.GameNotFound -> Problem.response(404, Problem.gameNotFound)
            GetGameError.PlayerNotFound -> Problem.response(404, Problem.userNotFound)
        }

    /**
     * Translates the errors of a Leave lobby action into a response
     * @receiver The error
     * @return The response
     */
    private fun LeaveLobbyError.resolveProblem(): ResponseEntity<*> =
        when (this) {
            LeaveLobbyError.LobbyNotFound -> Problem.response(404, Problem.lobbyNotFound)
            LeaveLobbyError.UserNotInLobby -> Problem.response(404, Problem.userNotFound)
            LeaveLobbyError.LeaveLobbyFailed -> Problem.response(500, Problem.leaveLobbyFailed)
        }
}
