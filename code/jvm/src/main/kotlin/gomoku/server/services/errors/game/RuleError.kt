package gomoku.server.services.errors.game

/**
 * Error for getting a rule
 */
sealed class RuleError {
    object RuleNotFound : RuleError()
}
