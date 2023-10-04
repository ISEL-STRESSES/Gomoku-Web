package gomoku.server.services.user

import gomoku.server.domain.user.Token
import gomoku.server.domain.user.User
import gomoku.server.domain.user.UserDomain
import gomoku.server.domain.user.UserExternalInfo
import gomoku.server.repository.TransactionManager
import gomoku.server.services.errors.TokenCreationError
import gomoku.server.services.errors.UserCreationError
import gomoku.utils.failure
import gomoku.utils.success
import kotlinx.datetime.Clock
import org.springframework.stereotype.Service

@Service
class UserService(
    private val transactionManager: TransactionManager,
    private val userDomain: UserDomain,
    private val clock: Clock
) {
    fun createUser(username: String, password: String): UserCreationResult {
        // Validate username
        if (!userDomain.isUsernameValid(username)) {
            return failure(UserCreationError.InvalidUsername)
        }
        // Validate password
        if (!userDomain.isSafePassword(password)) {
            return failure(UserCreationError.InvalidPassword)
        }
        // Hash the password
        val passwordValidationInfo = userDomain.createPasswordValidationInfo(password)

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
            if (!userDomain.validatePassword(password, user.passwordValidationInfo)) {
                failure(TokenCreationError.UserOrPasswordInvalid)
            }
            val tokenValue = userDomain.generateTokenValue()
            val now = clock.now()
            val token = Token(
                tokenValidationInfo = userDomain.createTokenValidationInfo(tokenValue),
                userId = user.uuid,
                createdAt = now,
                lastUsedAt = now
            )
            usersRepository.createToken(token, userDomain.maxNumberOfTokensPerUser)
            success(TokenExternalInfo(tokenValue, userDomain.getTokenExpiration(token)))
        }
    }

    fun getUsersData(offset: Int = DEFAULT_OFFSET, limit: Int = DEFAULT_LIMIT): List<UserExternalInfo> {
        val users = transactionManager.run {
            val usersRepository = it.usersRepository
            usersRepository.findUsersExternalInfo(offset, limit)
        }
        return users
    }

    fun getUserByToken(token: String): User? {
        if (!userDomain.canBeToken(token)) {
            return null
        }
        return transactionManager.run {
            val usersRepository = it.usersRepository
            val tokenValidationInfo = userDomain.createTokenValidationInfo(token)
            val userAndToken = usersRepository.getTokenByTokenValidationInfo(tokenValidationInfo)
            if (userAndToken != null && userDomain.isTokenTimeValid(clock, userAndToken.second)) {
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
