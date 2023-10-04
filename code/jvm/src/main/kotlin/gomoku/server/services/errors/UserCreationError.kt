package gomoku.server.services.errors

sealed class UserCreationError {
    object UsernameAlreadyExists : UserCreationError()
    object InvalidUsername : UserCreationError()
    object InvalidPassword : UserCreationError()
}
