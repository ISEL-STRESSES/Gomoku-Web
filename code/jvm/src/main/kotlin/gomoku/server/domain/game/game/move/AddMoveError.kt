package gomoku.server.domain.game.game.move

/**
 * Represents an error that can occur when adding a move to the board.
 * @property ImpossiblePosition the position is outside the board
 * @property AlreadyOccupied the position is already occupied
 */
sealed class AddMoveError {
    object ImpossiblePosition : AddMoveError()
    object AlreadyOccupied : AddMoveError()
}
