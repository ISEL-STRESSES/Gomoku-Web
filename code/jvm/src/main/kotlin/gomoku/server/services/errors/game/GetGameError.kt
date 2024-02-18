package gomoku.server.services.errors.game

/**
 * Error for getting a game
 */
sealed class GetGameError {
    object PlayerNotFound : GetGameError()
    object GameNotFound : GetGameError()
    object PlayerNotInGame : GetGameError()
}
