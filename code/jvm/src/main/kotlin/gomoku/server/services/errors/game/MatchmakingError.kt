package gomoku.server.services.errors.game

/**
 * Error for matchmaking
 */
sealed class MatchmakingError {
    object SamePlayer : MatchmakingError()
    object LeaveLobbyFailed : MatchmakingError()
    object LobbyNotFound : MatchmakingError()
    object LobbySateChangeFailed : MatchmakingError()
}
