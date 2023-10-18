package gomoku.server.services.errors.game

/**
 * Error for not finding a match
 */
sealed class MatchNotFoundError {
    object GameMatchNotFound : MatchNotFoundError()
}
