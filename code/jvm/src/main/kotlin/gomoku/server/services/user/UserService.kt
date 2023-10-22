package gomoku.server.services.user

import gomoku.server.domain.user.RankingUserData
import gomoku.server.domain.user.Token
import gomoku.server.domain.user.User
import gomoku.server.domain.user.UserStats
import gomoku.server.domain.user.UsersDomain
import gomoku.server.repository.TransactionManager
import gomoku.server.services.errors.user.TokenCreationError
import gomoku.server.services.errors.user.UserCreationError
import gomoku.server.services.errors.user.UserRankingServiceError
import gomoku.utils.failure
import gomoku.utils.success
import kotlinx.datetime.Clock
import org.springframework.stereotype.Service

/**
 * Service for user-related operations
 * @param transactionManager The transaction manager
 * @param usersDomain The domain for user-related operations
 * @param clock The clock
 */
@Service
class UserService(
    private val transactionManager: TransactionManager,
    private val usersDomain: UsersDomain,
    private val clock: Clock
) {

    /**
     * Creates a user with the given username and password.
     * @param username The username of the user.
     * @param password The password of the user.
     * @return The result of the operation, Either a [UserCreationError] or the id of the created user.
     * @see UserCreationResult
     */
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
            if (it.usersRepository.isUserStoredByUsername(username)) {
                failure(UserCreationError.UsernameAlreadyExists)
            } else {
                val uuid = it.usersRepository.storeUser(username, passwordValidationInfo)
                success(uuid)
            }
        }
    }

    /**
     * Creates a token for the given username and password.
     * @param username The username of the user.
     * @param password The password of the user.
     * @return The result of the operation, Either a [TokenCreationError] or the token.
     * @see TokenCreationResult
     */
    fun createToken(username: String, password: String): TokenCreationResult {
        if (username.isBlank() || password.isBlank()) {
            return failure(TokenCreationError.UserOrPasswordInvalid)
        }
        return transactionManager.run {
            val user = it.usersRepository.getUserByUsername(username)
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
            it.usersRepository.createToken(token, usersDomain.maxNumberOfTokensPerUser)
            success(TokenExternalInfo(tokenValue, usersDomain.getTokenExpiration(token)))
        }
    }

    /**
     * Gets the stats of the user for every rule.
     * @param userId The id of the user.
     * @return The stats of the user for every rule, or null if the user doesn't exist.
     */
    fun getUserStats(userId: Int): UserStats? =
        transactionManager.run {
            it.usersRepository.getUserStats(userId)
        }

    /**
     * Gets the stats of the user for a given rule.
     * @param userId The id of the user.
     * @param ruleId The id of the rule.
     * @return The stats of the user for the given rule, or null if the user doesn't exist.
     */
    fun getUserRanking(userId: Int, ruleId: Int): UserRankingResult =
        transactionManager.run {
            if (!it.usersRepository.isUserStoredById(userId)) {
                return@run failure(UserRankingServiceError.UserNotFound)
            }
            if (it.matchRepository.getRuleById(ruleId) == null) {
                return@run failure(UserRankingServiceError.RuleNotFound)
            }
            val stats = it.usersRepository.getUserRanking(userId, ruleId)
            if (stats == null) {
                return@run failure(UserRankingServiceError.UserStatsNotFound)
            } else {
                return@run success(stats)
            }
        }

    /**
     * Searches for users stats by their username in a specific rule.
     * @param ruleId The id of the rule.
     * @param username The username of the users.
     * @param offset The offset of the first user to get.
     * @param limit The maximum number of users to get.
     * @return A list of [UserStats] objects, containing all the stats related to the users, or null if the ruleId is invalid.
     */
    fun searchRanking(ruleId: Int, username: String?, offset: Int = DEFAULT_OFFSET, limit: Int = DEFAULT_LIMIT): List<RankingUserData>? =
        transactionManager.run {
            val availableRules = it.matchRepository.getAllRules()
            if (availableRules.any { rule -> rule.ruleId == ruleId }) {
                return@run it.usersRepository.searchRanking(ruleId, username ?: "", offset, limit)
            } else {
                null
            }
        }

    /**
     * Gets a user by its id.
     * @param id The id of the user.
     * @return The user, or null if the user doesn't exist.
     */
    fun getUserById(id: Int): User? {
        return transactionManager.run {
            it.usersRepository.getUserById(id)
        }
    }

    /**
     * Gets a user by its token.
     * @param token The token of the user.
     * @return The user, or null if the user doesn't exist.
     */
    fun getUserByToken(token: String): User? {
        if (!usersDomain.canBeToken(token)) {
            return null
        }
        return transactionManager.run {
            val tokenValidationInfo = usersDomain.createTokenValidationInfo(token)
            val userAndToken = it.usersRepository.getTokenAndUserByTokenValidationInfo(tokenValidationInfo)
            if (userAndToken != null && usersDomain.isTokenTimeValid(clock, userAndToken.second)) {
                it.usersRepository.updateTokenLastUsed(userAndToken.second, clock.now())
                userAndToken.first
            } else {
                null
            }
        }
    }

    /**
     * Revokes a token.
     * @param token The token to revoke.
     * @return True if the token was revoked, false otherwise.
     */
    fun revokeToken(token: String): Boolean {
        val tokenValidationInfo = usersDomain.createTokenValidationInfo(token)
        return transactionManager.run {
            it.usersRepository.removeTokenByTokenValidationInfo(tokenValidationInfo) == 1
        }
    }

    companion object {
        const val DEFAULT_OFFSET = 0
        const val DEFAULT_LIMIT = 10
    }
}
