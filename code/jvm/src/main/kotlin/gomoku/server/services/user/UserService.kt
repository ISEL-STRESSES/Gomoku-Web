package gomoku.server.services.user

import gomoku.server.domain.user.Password
import gomoku.server.domain.user.Token
import gomoku.server.repository.TransactionManager
import gomoku.server.services.user.dtos.get.GetUserOutputDTO
import gomoku.server.services.user.dtos.get.GetUsersOutputDTO
import gomoku.server.services.user.dtos.login.UserLoginInputDTO
import gomoku.server.services.user.dtos.login.UserLoginOutputDTO
import gomoku.server.services.user.dtos.register.UserRegisterInputDTO
import gomoku.server.services.user.dtos.register.UserRegisterOutputDTO
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val transactionManager: TransactionManager,
    private val passwordEncoder: PasswordEncoder
) {
    fun registerUser(registerInputDTO: UserRegisterInputDTO): UserRegisterOutputDTO {
        // Hash the password
        val hashedPassword = passwordEncoder.encode(registerInputDTO.password)

        //TODO Generate and hash token in proper way
        val token = generateTokenForUser(registerInputDTO.username)
        val hashedToken = "" //hash token

        transactionManager.run {
            val uuid = it.userRepository.save(registerInputDTO.username)
            it.authenticationRepository.save(uuid, Token(hashedToken), Password(hashedPassword))

        }

        return UserRegisterOutputDTO(token)
    }

    fun loginUser(loginInputDTO: UserLoginInputDTO): UserLoginOutputDTO {
        // Fetch user's hashed password from database

        return transactionManager.run {
            val user = it.userRepository.findUserByUsername(loginInputDTO.username)
                ?: throw IllegalArgumentException("Invalid credentials")

            val encodedPassword = it.authenticationRepository.getPassword(user.uuid)
                ?: throw IllegalArgumentException("Invalid credentials")

            if (passwordEncoder.matches(loginInputDTO.password, encodedPassword.encodedPassword)) {
                //TODO: Generate and encode token
                val token = generateTokenForUser(user.username)
                val hashedToken = "" //hash token
                it.authenticationRepository.setToken(user.uuid, Token(hashedToken))
                UserLoginOutputDTO(token)
            } else {
                throw IllegalArgumentException("Invalid credentials")
            }
        }
    }
    fun getRanking(offset: Int = DEFAULT_OFFSET, limit: Int = DEFAULT_LIMIT): GetUsersOutputDTO {

        return transactionManager.run {
            val users = it.userRepository.getRanking(offset, limit)
            GetUsersOutputDTO(users)
        }
    }

    fun getUser(uuid: Int): GetUserOutputDTO {
        val user = transactionManager.run {
            // Fetch user by ID
            it.userRepository.findUserById(uuid)
                ?: throw IllegalArgumentException("User not found") //TODO: use NotFoundException

        }
        return GetUserOutputDTO(user)

    }

    fun logoutUser(token: String) {
        TODO()
        //invalidate token
    }

    private fun generateTokenForUser(username: String): String {
        //TODO: Use JWT to generate token
        return "$username-token"
    }

    companion object {
        const val DEFAULT_OFFSET = 0
        const val DEFAULT_LIMIT = 10
    }
}
