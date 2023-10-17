package gomoku.server.services.errors.game

sealed class MatchNotFoundError {
    object GameMatchNotFound : MatchNotFoundError()
}
