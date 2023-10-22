package gomoku.server.domain.game

import gomoku.server.domain.game.errors.MoveError
import gomoku.utils.Either

/**
 * Represents the result of a move validation
 */
typealias IsValidMoveResult = Either<MoveError, Unit>
