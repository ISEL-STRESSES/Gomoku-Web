package gomoku.server.services.errors.lobby

/**
 * Error for join lobby
 */
sealed class JoinLobbyError {
    object LobbyNotFound : JoinLobbyError()
    object UserAlreadyInLobby : JoinLobbyError()
    object JoinLobbyFailed : JoinLobbyError()
}
