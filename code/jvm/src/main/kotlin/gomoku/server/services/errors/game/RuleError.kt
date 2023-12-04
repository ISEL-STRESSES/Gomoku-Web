package gomoku.server.services.errors.game

sealed class RuleError {
    object RuleNotFound : RuleError()
}