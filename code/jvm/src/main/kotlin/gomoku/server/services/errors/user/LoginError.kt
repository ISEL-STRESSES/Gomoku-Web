package gomoku.server.services.errors.user

/**
 * Error for login
 * TODO not used
 */
sealed class LoginError {
    object UserOrPasswordInvalid : LoginError()
}
