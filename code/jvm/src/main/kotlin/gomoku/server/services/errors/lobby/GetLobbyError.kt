package gomoku.server.services.errors.lobby

sealed class GetLobbyError {
    object LobbyNotFound : GetLobbyError()
}