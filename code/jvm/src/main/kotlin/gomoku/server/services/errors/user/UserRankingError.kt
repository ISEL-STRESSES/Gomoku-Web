package gomoku.server.services.errors.user

/**
 * Error for user stats
 */
sealed class UserRankingError {
    object UserNotFound : UserRankingError()

    object RuleNotFound : UserRankingError()

    object UserStatsNotFound : UserRankingError()
}
