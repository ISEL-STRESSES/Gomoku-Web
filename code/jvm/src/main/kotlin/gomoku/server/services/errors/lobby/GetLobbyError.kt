package gomoku.server.services.errors.lobby

/**
 * Error for get lobby
 */
sealed class GetLobbyError {
    object LobbyNotFound : GetLobbyError()
}
