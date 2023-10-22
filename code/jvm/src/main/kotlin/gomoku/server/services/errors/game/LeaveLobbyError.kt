package gomoku.server.services.errors.game

/**
 * Error for leaving a lobby
 */
sealed class LeaveLobbyError {
    object LobbyNotFound : LeaveLobbyError()
    object UserNotInLobby : LeaveLobbyError()
    object LeaveLobbyFailed : LeaveLobbyError()
}
