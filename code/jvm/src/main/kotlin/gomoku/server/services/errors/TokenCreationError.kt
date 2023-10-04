package gomoku.server.services.errors

sealed class TokenCreationError {
    object UserOrPasswordInvalid : TokenCreationError()
}