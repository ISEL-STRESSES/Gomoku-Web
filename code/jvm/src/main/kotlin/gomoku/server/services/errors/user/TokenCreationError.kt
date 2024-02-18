package gomoku.server.services.errors.user

/**
 * Error for token creation
 */
sealed class TokenCreationError {
    object UserOrPasswordInvalid : TokenCreationError()
}
