package gomoku.server.http.controllers.lobby

import gomoku.server.domain.user.AuthenticatedUser
import gomoku.server.http.URIs
import gomoku.server.http.controllers.game.models.MatchmakerOutputModel
import gomoku.server.http.controllers.lobby.models.GetLobbiesOutput
import gomoku.server.http.controllers.lobby.models.LobbyIdInputModel
import gomoku.server.http.controllers.lobby.models.RuleIdInputModel
import gomoku.server.http.controllers.media.Problem
import gomoku.server.http.responses.CreateLobby
import gomoku.server.http.responses.GetLobbies
import gomoku.server.http.responses.GetLobbyById
import gomoku.server.http.responses.JoinLobby
import gomoku.server.http.responses.LeaveLobby
import gomoku.server.http.responses.Matchmaker
import gomoku.server.http.responses.response
import gomoku.server.http.responses.responseRedirect
import gomoku.server.services.errors.game.MatchmakingError
import gomoku.server.services.errors.lobby.GetLobbyError
import gomoku.server.services.errors.lobby.JoinLobbyError
import gomoku.server.services.errors.lobby.LeaveLobbyError
import gomoku.server.services.lobby.LobbyService
import gomoku.utils.Failure
import gomoku.utils.Success
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Controller for lobby-related endpoints
 */
@RestController(value = "Lobbies")
@RequestMapping(URIs.Lobby.ROOT)
class LobbyController(private val lobbyService: LobbyService) {

    /**
     * Leaves a lobby
     * @param lobbyIdModel The id of the lobby
     * @param authenticatedUser The authenticated user
     * @return The result of leaving the lobby
     */
    @PostMapping(URIs.Lobby.LEAVE_LOBBY)
    fun leaveLobby(@RequestBody lobbyIdModel: LobbyIdInputModel, authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        val leftLobby = lobbyService.leaveLobby(lobbyIdModel.lobbyId, authenticatedUser.user.uuid)
        return when (leftLobby) {
            is Failure -> leftLobby.value.resolveProblem()
            is Success -> LeaveLobby.siren(leftLobby.value).responseRedirect(200, URIs.Game.ROOT + URIs.Game.HUB)
        }
    }

    /**
     * Joins a lobby
     * @param lobbyIdModel the id of the lobby
     * @param authenticatedUser the authenticated user
     * @return the result of joining a lobby
     */
    @PostMapping(URIs.Lobby.JOIN_LOBBY)
    fun joinLobby(@RequestBody lobbyIdModel: LobbyIdInputModel, authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        val joinedLobby = lobbyService.joinLobby(lobbyIdModel.lobbyId, authenticatedUser.user.uuid)
        return when (joinedLobby) {
            is Failure -> joinedLobby.value.resolveProblem()
            is Success -> JoinLobby.siren(joinedLobby.value).responseRedirect(201, URIs.Game.ROOT + "/${joinedLobby.value.id}")
        }
    }

    /**
     * Get all the available lobbies
     *
     * @param authenticatedUser the authenticated user
     * @return the result of the fetch of all lobbies
     */
    @GetMapping(URIs.Lobby.GET_LOBBIES)
    fun getLobbies(authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        val lobbies = lobbyService.getLobbiesByUserId(authenticatedUser.user.uuid)
        return GetLobbies.siren(GetLobbiesOutput(lobbies)).response(200)
    }

    /**
     * Create a lobby
     *
     * @param ruleIdInput the id of the rule
     * @param authenticatedUser the authenticated user
     * @return the result of the creation of a lobby
     */
    @PostMapping(URIs.Lobby.CREATE_LOBBY)
    fun createLobby(@RequestBody ruleIdInput: RuleIdInputModel, authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        val createLobbyResult = lobbyService.createLobby(ruleIdInput.ruleId, authenticatedUser.user.uuid)
        return CreateLobby.siren(createLobbyResult).responseRedirect(201, URIs.HOME + "/${createLobbyResult.id}")
    }

    /**
     * Starts the matchmaking process for a game, either
     * by creating a new lobby or joining a game
     * @param ruleIdInput The id of the rule
     * @param authenticatedUser The authenticated user
     * @return The result of the matchmaking process
     */
    @PostMapping(URIs.Lobby.MATCH_MAKE)
    fun startMatchmaking(@RequestBody ruleIdInput: RuleIdInputModel, authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        val matchmaker = lobbyService.startMatchmakingProcess(ruleIdInput.ruleId, authenticatedUser.user.uuid)
        return when (matchmaker) {
            is Failure -> matchmaker.value.resolveProblem()
            is Success -> if (matchmaker.value.isGame) {
                Matchmaker.siren(MatchmakerOutputModel(matchmaker.value)).responseRedirect(201, URIs.Game.ROOT + "/${matchmaker.value.id}")
            } else {
                Matchmaker.siren(MatchmakerOutputModel(matchmaker.value)).responseRedirect(201, URIs.Lobby.ROOT + "/${matchmaker.value.id}")
            }
        }
    }

    /**
     * Get the lobby by its id
     *
     * @param lobbyId the id of the lobby
     * @param authenticatedUser the authenticated user
     * @return the return of the get lobby by id operation
     */
    @GetMapping(URIs.Lobby.GET_LOBBY_BY_ID)
    fun getLobbyById(@PathVariable lobbyId: Int, authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        val lobby = lobbyService.getLobbyById(lobbyId)
        return when (lobby) {
            is Failure -> lobby.value.resolveProblem()
            is Success -> GetLobbyById.siren(lobby.value).responseRedirect(200, URIs.Lobby.GET_LOBBY_BY_ID + "/${lobby.value.id}")
        }
    }

    /**
     * Translates the errors of a Matchmaking action into a response
     * @receiver The error
     * @return The response
     */
    private fun MatchmakingError.resolveProblem(): ResponseEntity<*> =
        when (this) {
            MatchmakingError.SamePlayer -> Problem.response(400, Problem.samePlayer)
            MatchmakingError.LobbyNotFound -> Problem.response(404, Problem.lobbyNotFound)
            MatchmakingError.LeaveLobbyFailed -> Problem.response(500, Problem.leaveLobbyFailed)
            MatchmakingError.LobbySateChangeFailed -> Problem.response(500, Problem.lobbySateChangeFailed)
        }

    /**
     * Translates the errors of a Leave lobby action into a response
     * @receiver The error
     * @return The response
     */
    private fun LeaveLobbyError.resolveProblem(): ResponseEntity<*> =
        when (this) {
            LeaveLobbyError.LobbyNotFound -> Problem.response(404, Problem.lobbyNotFound)
            LeaveLobbyError.UserNotInLobby -> Problem.response(404, Problem.userNotInLobby)
            LeaveLobbyError.LeaveLobbyFailed -> Problem.response(500, Problem.leaveLobbyFailed)
        }

    /**
     * Translates the errors of joining a lobby action into a response
     * @receiver The error
     * @return The response
     */
    private fun JoinLobbyError.resolveProblem(): ResponseEntity<*> =
        when (this) {
            JoinLobbyError.LobbyNotFound -> Problem.response(404, Problem.lobbyNotFound)
            JoinLobbyError.UserAlreadyInLobby -> Problem.response(409, Problem.userAlreadyInLobby)
            JoinLobbyError.JoinLobbyFailed -> Problem.response(500, Problem.joinLobbyFailed)
            JoinLobbyError.LobbySateChangeFailed -> Problem.response(500, Problem.lobbySateChangeFailed)
        }

    /**
     * Translates the errors of get the lobby action into a response
     * @receiver The error
     * @return The response
     */
    private fun GetLobbyError.resolveProblem(): ResponseEntity<*> =
        when (this) {
            GetLobbyError.LobbyNotFound -> Problem.response(404, Problem.lobbyNotFound)
        }
}
