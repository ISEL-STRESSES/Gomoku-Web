import gomoku.server.repository.authentication.AuthenticationRepository
import gomoku.server.repository.user.UserRepository

interface Transaction {

    val userRepository: UserRepository
    val authenticationRepository: AuthenticationRepository

    fun rollback()
}
