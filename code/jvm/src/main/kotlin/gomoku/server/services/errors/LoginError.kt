package gomoku.server.services.errors

sealed class LoginError {
    object UserOrPasswordInvalid : LoginError()
}
