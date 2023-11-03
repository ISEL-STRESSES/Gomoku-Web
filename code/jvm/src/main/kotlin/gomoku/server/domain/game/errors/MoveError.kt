package gomoku.server.domain.game.errors

/**
 * Represents an error that can occur when trying to play a move
 * @property InvalidPosition the position is invalid in this set of rules
 * @property AlreadyOccupied the position is already occupied
 * @property InvalidTurn the player is trying to play out of turn
 */
sealed class MoveError {
    object InvalidPosition : MoveError()
    object AlreadyOccupied : MoveError()
    object InvalidTurn : MoveError()
    object InvalidMove : MoveError()
}
