package gomoku.server.services.errors.user

sealed class LoginError {
    object UserOrPasswordInvalid : LoginError()
}
