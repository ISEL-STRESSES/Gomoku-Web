package gomoku.server.services.errors.game

/**
 * Error for getting a match
 */
sealed class GetMatchError {
    object PlayerNotFound : GetMatchError()

    object MatchNotFound : GetMatchError()

    object PlayerNotInMatch : GetMatchError()
}