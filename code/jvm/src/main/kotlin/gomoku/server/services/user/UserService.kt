package gomoku.server.services.user

import gomoku.server.domain.user.Token
import gomoku.server.domain.user.User
import gomoku.server.domain.user.UserData
import gomoku.server.domain.user.UsersDomain
import gomoku.server.repository.TransactionManager
import gomoku.server.services.errors.user.TokenCreationError
import gomoku.server.services.errors.user.UserCreationError
import gomoku.utils.failure
import gomoku.utils.success
import kotlinx.datetime.Clock
import org.springframework.stereotype.Service

@Service
class UserService(
    private val transactionManager: TransactionManager,
    private val usersDomain: UsersDomain,
    private val clock: Clock
) {
    fun createUser(username: String, password: String): UserCreationResult {
        // Validate username
        if (!usersDomain.isUsernameValid(username)) {
            return failure(UserCreationError.InvalidUsername)
        }
        // Validate password
        if (!usersDomain.isSafePassword(password)) {
            return failure(UserCreationError.InvalidPassword)
        }
        // Hash the password
        val passwordValidationInfo = usersDomain.createPasswordValidationInfo(password)

        return transactionManager.run {
            val usersRepository = it.usersRepository
            if (usersRepository.isUserStoredByUsername(username)) {
                failure(UserCreationError.UsernameAlreadyExists)
            } else {
                val uuid = usersRepository.storeUser(username, passwordValidationInfo)
                success(uuid)
            }
        }
    }

    fun createToken(username: String, password: String): TokenCreationResult {
        if (username.isBlank() || password.isBlank()) {
            return failure(TokenCreationError.UserOrPasswordInvalid)
        }
        return transactionManager.run {
            val usersRepository = it.usersRepository
            val user = usersRepository.getUserByUsername(username)
                ?: return@run failure(TokenCreationError.UserOrPasswordInvalid)
            if (!usersDomain.validatePassword(password, user.passwordValidationInfo)) {
                failure(TokenCreationError.UserOrPasswordInvalid)
            }
            val tokenValue = usersDomain.generateTokenValue()
            val now = clock.now()
            val token = Token(
                tokenValidationInfo = usersDomain.createTokenValidationInfo(tokenValue),
                userId = user.uuid,
                createdAt = now,
                lastUsedAt = now
            )
            usersRepository.createToken(token, usersDomain.maxNumberOfTokensPerUser)
            success(TokenExternalInfo(tokenValue, usersDomain.getTokenExpiration(token)))
        }
    }

    // TODO: GETUSERSSTATSDATA
    fun getUsersData(offset: Int = DEFAULT_OFFSET, limit: Int = DEFAULT_LIMIT): List<UserData> {
        val users = transactionManager.run {
            val usersRepository = it.usersRepository
            usersRepository.getUsersStatsData(offset, limit)
        }
        return users
    }

    fun getUserById(id: Int): UserData? {
        return transactionManager.run {
            val usersRepository = it.usersRepository
            usersRepository.getUserById(id)
        }
    }

    fun getUserByToken(token: String): User? {
        if (!usersDomain.canBeToken(token)) {
            return null
        }
        return transactionManager.run {
            val usersRepository = it.usersRepository
            val tokenValidationInfo = usersDomain.createTokenValidationInfo(token)
            val userAndToken = usersRepository.getTokenAndUserByTokenValidationInfo(tokenValidationInfo)
            if (userAndToken != null && usersDomain.isTokenTimeValid(clock, userAndToken.second)) {
                usersRepository.updateTokenLastUsed(userAndToken.second, clock.now())
                userAndToken.first
            } else {
                null
            }
        }
    }

    fun revokeToken(token: String) {
    }

    companion object {
        const val DEFAULT_OFFSET = 0
        const val DEFAULT_LIMIT = 10
    }
}
