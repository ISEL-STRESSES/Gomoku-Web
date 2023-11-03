package gomoku.server.services.errors.game

/**
 * Error for getting the current turn player
 */
sealed class CurrentTurnPlayerError {
    object GameAlreadyFinished : CurrentTurnPlayerError()
    object GameNotFound : CurrentTurnPlayerError()
}
