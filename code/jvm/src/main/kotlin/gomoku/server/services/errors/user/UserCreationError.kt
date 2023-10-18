package gomoku.server.services.errors.user

/**
 * Error for user creation
 */
sealed class UserCreationError {
    object UsernameAlreadyExists : UserCreationError()
    object InvalidUsername : UserCreationError()
    object InvalidPassword : UserCreationError()
}
