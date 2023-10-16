package gomoku.server.services.errors.user

sealed class TokenCreationError {
    object UserOrPasswordInvalid : TokenCreationError()
}
