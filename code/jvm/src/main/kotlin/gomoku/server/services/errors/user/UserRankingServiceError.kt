package gomoku.server.services.errors.user

/**
 * Error for user stats
 */
sealed class UserRankingServiceError {
    object UserNotFound : UserRankingServiceError()

    object RuleNotFound : UserRankingServiceError()

    object UserStatsNotFound : UserRankingServiceError()
}
