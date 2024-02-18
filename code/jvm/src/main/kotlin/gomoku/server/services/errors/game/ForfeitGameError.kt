package gomoku.server.services.errors.game

/**
 * Error for forfeiting a game
 */
sealed class ForfeitGameError {
    object GameAlreadyFinished : ForfeitGameError()
    object GameNotFound : ForfeitGameError()
    object PlayerNotInGame : ForfeitGameError()
    object PlayerNotFound : ForfeitGameError()
}
