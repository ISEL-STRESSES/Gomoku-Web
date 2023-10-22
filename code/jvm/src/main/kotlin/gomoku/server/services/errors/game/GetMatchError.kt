package gomoku.server.services.errors.game

sealed class GetMatchError {
    object PlayerNotFound : GetMatchError()

    object MatchNotFound : GetMatchError()

    object PlayerNotInMatch : GetMatchError()
}