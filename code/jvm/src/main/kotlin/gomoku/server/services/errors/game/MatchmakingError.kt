package gomoku.server.services.errors.game

/**
 * Error for matchmaking
 */
sealed class MatchmakingError {
    object SamePlayer : MatchmakingError()
    object LeaveLobbyFailed : MatchmakingError()

    object LobbySateChangeFailed : MatchmakingError()
}
