package gomoku.server.services.errors.game

/**
 * Error for not finding a game
 */
sealed class GameNotFoundError {
    object GameNotFound : GameNotFoundError()
}
