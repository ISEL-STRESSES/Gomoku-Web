package gomoku.server.services.errors.lobby

sealed class JoinLobbyError {
    object LobbyNotFound : JoinLobbyError()
    object UserAlreadyInLobby : JoinLobbyError()
    object JoinLobbyFailed : JoinLobbyError()
}
