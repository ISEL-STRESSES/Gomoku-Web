package gomoku.server.services.errors.game

/**
 * Error for not finding a game
 * TODO not used
 */
sealed class GameNotFoundError {
    object GameNotFound : GameNotFoundError()
}
