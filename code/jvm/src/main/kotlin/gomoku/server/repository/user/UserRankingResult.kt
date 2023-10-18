package gomoku.server.repository.user

import gomoku.server.domain.user.UserRuleStats
import gomoku.utils.Either

typealias UserRankingResult = Either<UserRankingError, UserRuleStats>

sealed class UserRankingError {
    object UserNotFound : UserRankingError()
    object UserHasNoGamesInRule : UserRankingError()
}