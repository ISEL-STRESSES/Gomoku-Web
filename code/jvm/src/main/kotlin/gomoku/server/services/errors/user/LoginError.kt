package gomoku.server.services.errors.user

/**
 * Error for login
 */
sealed class LoginError {
    object UserOrPasswordInvalid : LoginError()
}
