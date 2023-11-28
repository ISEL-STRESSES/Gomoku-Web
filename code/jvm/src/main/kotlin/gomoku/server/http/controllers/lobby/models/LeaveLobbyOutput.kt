package gomoku.server.http.controllers.lobby.models

/**
 * Represents the output model of a leave lobby to be sent from the API
 * @property lobbyId the lobby id to leave from.
 * @property userId the user that what's to leave the lobby.
 */
data class LeaveLobbyOutput(
    val lobbyId: Int,
    val userId: Int
)
