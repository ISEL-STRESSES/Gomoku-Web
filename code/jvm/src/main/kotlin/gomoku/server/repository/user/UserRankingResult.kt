package gomoku.server.repository.user

import gomoku.server.domain.user.UserStats
import gomoku.utils.Either

/**
 * Represents the result of getting a user's ranking.
 */
typealias UserRankingResult = Either<UserRankingError, UserStats>

/**
 * Represents the possible errors that can occur when trying to get a user's ranking.
 * @see UserRankingResult
 * @see UserRankingError
 */
sealed class UserRankingError {
    object UserNotFound : UserRankingError()
    object UserHasNoGamesInRule : UserRankingError()
}
