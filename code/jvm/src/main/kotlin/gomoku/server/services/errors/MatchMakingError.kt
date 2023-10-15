package gomoku.server.services.errors

sealed class MatchmakingError {
    object SamePlayer : MatchmakingError()
}