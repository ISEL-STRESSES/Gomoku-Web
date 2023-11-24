package gomoku.server.http.controllers.lobby

import gomoku.server.domain.user.AuthenticatedUser
import gomoku.server.http.URIs
import gomoku.server.http.controllers.lobby.models.GetLobbiesOutput
import gomoku.server.http.controllers.media.Problem
import gomoku.server.http.responses.CreateLobby
import gomoku.server.http.responses.GetLobbies
import gomoku.server.http.responses.GetLobbyById
import gomoku.server.http.responses.JoinLobby
import gomoku.server.http.responses.LeaveLobby
import gomoku.server.http.responses.response
import gomoku.server.http.responses.responseRedirect
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
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController(value = "Lobbies")
@RequestMapping(URIs.HOME)
class LobbyController(private val lobbyService: LobbyService) {
    /**
     * Leaves a lobby
     * @param lobbyId The id of the lobby
     * @param authenticatedUser The authenticated user
     * @return The result of leaving the lobby
     */
    @PostMapping(URIs.Lobby.LEAVE_LOBBY)
    fun leaveLobby(@PathVariable lobbyId: Int, authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        val leftLobby = lobbyService.leaveLobby(lobbyId, authenticatedUser.user.uuid)
        return when (leftLobby) {
            is Failure -> leftLobby.value.resolveProblem()
            is Success -> LeaveLobby.siren(leftLobby.value).responseRedirect(200, URIs.Game.ROOT + URIs.Game.HUB)
        }
    }

    @PostMapping(URIs.Lobby.JOIN_LOBBY)
    fun joinLobby(@PathVariable lobbyId: Int, authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        val joinedLobby = lobbyService.joinLobby(lobbyId, authenticatedUser.user.uuid)
        return when (joinedLobby) {
            is Failure -> joinedLobby.value.resolveProblem()
            is Success -> JoinLobby.siren(joinedLobby.value).responseRedirect(201, URIs.Lobby.GET_LOBBY_BY_ID + "/${joinedLobby.value.id}")
        }
    }

    @GetMapping(URIs.Lobby.GET_LOBBIES)
    fun getLobbies(authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        val lobbies = lobbyService.getLobbies()
        return GetLobbies.siren(GetLobbiesOutput(lobbies)).response(200)
    }

    @PostMapping(URIs.Lobby.CREATE_LOBBY)
    fun createLobby(@PathVariable ruleId: Int, authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        val createLobbyResult = lobbyService.createLobby(ruleId, authenticatedUser.user.uuid)
        return CreateLobby.siren(createLobbyResult).responseRedirect(201, URIs.Lobby.GET_LOBBY_BY_ID + "/${createLobbyResult.id}")
    }

    @GetMapping(URIs.Lobby.GET_LOBBY_BY_ID)
    fun getLobbyById(@PathVariable lobbyId: Int, authenticatedUser: AuthenticatedUser): ResponseEntity<*> {
        val lobby = lobbyService.getLobbyById(lobbyId)
        return when (lobby) {
            is Failure -> lobby.value.resolveProblem()
            is Success -> GetLobbyById.siren(lobby.value).responseRedirect(200, URIs.Lobby.GET_LOBBY_BY_ID + "/${lobby.value.id}")
        }
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

    private fun JoinLobbyError.resolveProblem(): ResponseEntity<*> =
        when (this) {
            JoinLobbyError.LobbyNotFound -> Problem.response(404, Problem.lobbyNotFound)
            JoinLobbyError.UserAlreadyInLobby -> Problem.response(404, Problem.userAlreadyInLobby)
            JoinLobbyError.JoinLobbyFailed -> Problem.response(500, Problem.joinLobbyFailed)
        }

    private fun GetLobbyError.resolveProblem(): ResponseEntity<*> =
        when (this) {
            GetLobbyError.LobbyNotFound -> Problem.response(404, Problem.lobbyNotFound)
        }
}