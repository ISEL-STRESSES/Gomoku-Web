package gomoku.server.services.errors.game

sealed class MatchmakingError {
    object SamePlayer : MatchmakingError()
}