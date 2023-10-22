package gomoku.server.domain.game.game.move

import gomoku.utils.Either

/**
 * Represents the result of a move addition, using Either concept.
 * @see AddMoveError
 * @see MoveContainer
 */
typealias AddMoveResult = Either<AddMoveError, MoveContainer>
