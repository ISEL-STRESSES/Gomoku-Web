package gomoku.server.services.user

import gomoku.server.repository.authentication.AuthenticationRepository
import gomoku.server.repository.user.UserRepository
import gomoku.server.services.user.dtos.get.GetUserOutputDTO
import gomoku.server.services.user.dtos.get.GetUsersOutputDTO
import gomoku.server.services.user.dtos.login.UserLoginInputDTO
import gomoku.server.services.user.dtos.login.UserLoginOutputDTO
import gomoku.server.services.user.dtos.register.UserRegisterInputDTO
import gomoku.server.services.user.dtos.register.UserRegisterOutputDTO
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val authenticationRepository: AuthenticationRepository
) {

    private val passwordEncoder = BCryptPasswordEncoder()

    fun registerUser(registerInputDTO: UserRegisterInputDTO): UserRegisterOutputDTO {
        // Hash the password
        val hashedPassword = passwordEncoder.encode(registerInputDTO.password)

        //TODO Generate and hash token in proper way
        val token = generateTokenForUser(registerInputDTO.username)
        val hashedToken = "" //hash token

        if (userRepository.existsUserWithUsername(registerInputDTO.username)) {
            throw IllegalArgumentException("Username is already taken")
        }
        val uuid = userRepository.save(registerInputDTO.username)
        authenticationRepository.save(uuid, hashedPassword, hashedToken)

        return UserRegisterOutputDTO(token)
    }

    fun loginUser(loginInputDTO: UserLoginInputDTO): UserLoginOutputDTO {
        // Fetch user's hashed password from database

        val user = userRepository.findUserByUsername(loginInputDTO.username)
            ?: throw IllegalArgumentException("Invalid credentials")

        val encodedPassword = authenticationRepository.getPassword(user.uuid)
            ?: throw IllegalArgumentException("Invalid credentials")

        if (passwordEncoder.matches(loginInputDTO.password, encodedPassword)) {
            //TODO: Generate and encode token
            val token = generateTokenForUser(user.username)
            val hashedToken = "" //hash token
            authenticationRepository.setToken(user.uuid, hashedToken)
            return UserLoginOutputDTO(token)
        } else {
            throw IllegalArgumentException("Invalid credentials")
        }
    }

    //TODO: Make this not hardcoded
    fun getUsers(offset: Int = 0, limit: Int = 10): GetUsersOutputDTO {

        val users = userRepository.findUsers(offset, limit)

        return GetUsersOutputDTO(users)
    }

    fun getUser(uuid: Int): GetUserOutputDTO {
        // Fetch user by ID
        val user = userRepository.findUserById(uuid)
            ?: throw IllegalArgumentException("User not found") //TODO: use NotFoundException

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
}
