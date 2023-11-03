package gomoku.server.services.errors.game

/**
 * Error for making a move
 */
sealed class MakeMoveError {
    object InvalidTurn : MakeMoveError()
    object AlreadyOccupied : MakeMoveError()
    object ImpossiblePosition : MakeMoveError()
    object GameNotFound : MakeMoveError()
    object PlayerNotInGame : MakeMoveError()
    object PlayerNotFound : MakeMoveError()
    object GameFinished : MakeMoveError()
    object InvalidMove : MakeMoveError()
    object MakeMoveFailed : MakeMoveError()
}
