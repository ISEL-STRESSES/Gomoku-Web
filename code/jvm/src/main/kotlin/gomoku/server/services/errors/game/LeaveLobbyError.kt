package gomoku.server.services.errors.game

sealed class LeaveLobbyError {
    object LobbyNotFound : LeaveLobbyError()
    object UserNotInLobby: LeaveLobbyError()
    object LeaveLobbyFailed: LeaveLobbyError()
}