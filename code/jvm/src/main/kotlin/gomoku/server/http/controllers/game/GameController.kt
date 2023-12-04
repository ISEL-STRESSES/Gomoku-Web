package gomoku.server.http.controllers.game

import gomoku.server.domain.user.AuthenticatedUser
import gomoku.server.http.URIs
import gomoku.server.http.controllers.game.models.GameOutputModel
import gomoku.server.http.controllers.game.models.GetFinishedGamesOutputModel
import gomoku.server.http.controllers.game.models.GetRulesOutputModel
import gomoku.server.http.controllers.game.models.RuleOutputModel
import gomoku.server.http.controllers.media.Problem
import gomoku.server.http.responses.ForfeitGame
import gomoku.server.http.responses.GetFinishedGames
import gomoku.server.http.responses.GetGameById
import gomoku.server.http.responses.GetRuleById
import gomoku.server.http.responses.GetRules
import gomoku.server.http.responses.GetTurn
import gomoku.server.http.responses.MakeMove
import gomoku.server.http.responses.response
import gomoku.server.services.errors.game.CurrentTurnPlayerError
import gomoku.server.services.errors.game.ForfeitGameError
import gomoku.server.services.errors.game.GetGameError
import gomoku.server.services.errors.game.MakeMoveError
import gomoku.server.services.errors.game.RuleError
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
import kotlin.math.ceil

/**
 * Controller for game-related endpoints
 * @property gameService The game service
 */
@RestController(value = "Games")
@RequestMapping(URIs.Game.ROOT)
class GameController(private val gameService: GameService) {

    /**
     * Gets the list of finished games
     * @param authenticatedUser The authenticated user
     * @return The list of games
     */
    @GetMapping(URIs.Game.HUB)
    fun finishedGames(
        @RequestParam offset: Int?,
        @RequestParam limit: Int?,
        authenticatedUser: AuthenticatedUser
    ): ResponseEntity<*> {
        val (games, totalCount) = gameService.getUserFinishedGames(offset, limit, authenticatedUser.user.uuid)

        val currentOffset = offset ?: DEFAULT_OFFSET
        val currentLimit = limit ?: DEFAULT_LIMIT
        val totalPages = ceil(totalCount.toDouble() / 10).toInt()

        return GetFinishedGames.siren(
            GetFinishedGamesOutputModel(games.map { GameOutputModel.fromGame(it) }),
            totalPages,
            currentOffset,
            currentLimit
        ).response(200)
    }

    /**
     * Gets the details of a game
     * @param gameId The id of the game
     * @param authenticatedUser The authenticated user
     * @return The details of the game
     */
    @GetMapping(URIs.Game.GET_BY_ID)
    fun gameDetails(@PathVariable gameId: Int, authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        val game = gameService.getGame(gameId, authenticatedUser.user.uuid)
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
     * Gets the details of a rule
     * @param ruleId The id of the rule
     * @return The details of the rule
     */
    @GetMapping(URIs.Game.GAME_RULE_ID)
    fun ruleDetails(@PathVariable ruleId: Int): ResponseEntity<*> {
        val rule = gameService.getRule(ruleId)
        return when (rule) {
            is Failure -> rule.value.resolveProblem()
            is Success -> GetRuleById.siren(RuleOutputModel(rule.value)).response(200)
        }
    }

    /**
     * Makes a move in the context of a game
     * @param gameId The id of the game
     * @param authenticatedUser The authenticated user
     * @param x The x coordinate of the move
     * @param y The y coordinate of the move
     * @return The result of the move
     */
    @PostMapping(URIs.Game.MAKE_PLAY)
    fun makePlay(
        @PathVariable gameId: Int,
        authenticatedUser: AuthenticatedUser,
        @RequestParam x: Int,
        @RequestParam y: Int
    ): ResponseEntity<*> {
        val moveResult = gameService.makeMove(gameId, authenticatedUser.user.uuid, x, y)
        return when (moveResult) {
            is Failure -> moveResult.value.resolveProblem()
            is Success ->
                MakeMove.siren(GameOutputModel.fromGame(moveResult.value)).response(200)
        }
    }

    /**
     * Gets the current turn player id
     * @param gameId The id of the game
     * @param authenticatedUser The authenticated user
     * @return The current turn player id
     */
    @GetMapping(URIs.Game.TURN)
    fun currentTurnPlayerId(@PathVariable gameId: Int, authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        val currentTurnPlayerId = gameService.getCurrentTurnPlayerId(gameId, authenticatedUser.user.uuid)
        return when (currentTurnPlayerId) {
            is Failure -> currentTurnPlayerId.value.resolveProblem()
            is Success -> GetTurn.siren(currentTurnPlayerId.value).response(200)
        }
    }

    /**
     * Forfeits a game
     * @param gameId The id of the game
     * @param authenticatedUser The authenticated user
     * @return The result of the forfeit
     */
    @PostMapping(URIs.Game.FORFEIT_GAME)
    fun forfeit(@PathVariable gameId: Int, authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        val forfeitResult = gameService.forfeitGame(gameId, authenticatedUser.user.uuid)
        return when (forfeitResult) {
            is Failure -> forfeitResult.value.resolveProblem()
            is Success -> ForfeitGame.siren(GameOutputModel.fromGame(forfeitResult.value)).response(200)
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
            MakeMoveError.PlayerNotInGame -> Problem.response(401, Problem.playerNotInGame)
            MakeMoveError.GameNotFound -> Problem.response(404, Problem.gameNotFound)
            MakeMoveError.PlayerNotFound -> Problem.response(404, Problem.userNotFound)
            MakeMoveError.AlreadyOccupied -> Problem.response(409, Problem.positionOccupied)
            MakeMoveError.MakeMoveFailed -> Problem.response(500, Problem.makeMoveFailed)
        }

    /**
     * Translates the errors of a Current turn player action into a response
     * @receiver The error
     * @return The response
     */
    private fun CurrentTurnPlayerError.resolveProblem(): ResponseEntity<*> =
        when (this) {
            CurrentTurnPlayerError.GameAlreadyFinished -> Problem.response(400, Problem.gameAlreadyFinished)
            CurrentTurnPlayerError.PlayerNotInGame -> Problem.response(401, Problem.playerNotInGame)
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

    private fun RuleError.resolveProblem(): ResponseEntity<*> =
        when (this) {
            RuleError.RuleNotFound -> Problem.response(404, Problem.ruleNotFound)
        }

    /**
     * Translates the errors of a Forfeit game action into a response
     * @receiver The error
     * @return The response
     */
    private fun ForfeitGameError.resolveProblem(): ResponseEntity<*> =
        when (this) {
            ForfeitGameError.GameAlreadyFinished -> Problem.response(400, Problem.gameAlreadyFinished)
            ForfeitGameError.PlayerNotInGame -> Problem.response(401, Problem.playerNotInGame)
            ForfeitGameError.GameNotFound -> Problem.response(404, Problem.gameNotFound)
            ForfeitGameError.PlayerNotFound -> Problem.response(404, Problem.userNotFound)
        }

    companion object {
        const val DEFAULT_OFFSET = 0
        const val DEFAULT_LIMIT = 10
    }
}
