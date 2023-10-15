package gomoku.server.services.errors.user

sealed class UserCreationError {
    object UsernameAlreadyExists : UserCreationError()
    object InvalidUsername : UserCreationError()
    object InvalidPassword : UserCreationError()
}
