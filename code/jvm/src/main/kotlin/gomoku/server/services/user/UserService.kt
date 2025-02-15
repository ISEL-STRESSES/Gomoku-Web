package gomoku.server.services.user

import gomoku.server.domain.user.RankingUserData
import gomoku.server.domain.user.Token
import gomoku.server.domain.user.User
import gomoku.server.domain.user.UserStats
import gomoku.server.domain.user.UsersDomain
import gomoku.server.http.controllers.user.models.userCreate.UserCreateOutputModel
import gomoku.server.repository.TransactionManager
import gomoku.server.services.errors.user.TokenCreationError
import gomoku.server.services.errors.user.UserCreationError
import gomoku.server.services.errors.user.UserRankingError
import gomoku.utils.failure
import gomoku.utils.success
import kotlinx.datetime.Clock
import org.springframework.stereotype.Service

typealias RankingSearchResult = Pair<List<RankingUserData>, Int>?

/**
 * Service for user-related operations
 * @param transactionManager The transaction manager
 * @param usersDomain The domain for user-related operations
 * @param clock The clock
 */
@Service
class UserService(
    private val transactionManager: TransactionManager,
    val usersDomain: UsersDomain,
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
                val tokenValue = usersDomain.generateTokenValue()
                val token = createToken(uuid, tokenValue)
                it.usersRepository.createToken(token, usersDomain.maxNumberOfTokensPerUser)
                success(UserCreateOutputModel(uuid, username, tokenValue, usersDomain.getTokenExpiration(token)))
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
                return@run failure(TokenCreationError.UserOrPasswordInvalid)
            }
            val tokenValue = usersDomain.generateTokenValue()
            val token = createToken(user.uuid, tokenValue)
            it.usersRepository.createToken(token, usersDomain.maxNumberOfTokensPerUser)
            success(UserCreateOutputModel(user.uuid, user.username, tokenValue, usersDomain.getTokenExpiration(token)))
        }
    }

    /**
     * Gets the stats of the user for every rule.
     * @param userId The id of the user.
     * @return The stats of the user for every rule, or null if the user doesn't exist.
     */
    fun getUserStats(userId: Int): UserStats? =
        transactionManager.run {
            val user = it.usersRepository.getUserById(userId) ?: return@run null
            it.usersRepository.getUserStats(userId) ?: return@run UserStats(user.uuid, user.username, emptyList())
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
                return@run failure(UserRankingError.UserNotFound)
            }
            if (it.gameRepository.getRuleById(ruleId) == null) {
                return@run failure(UserRankingError.RuleNotFound)
            }
            val stats = it.usersRepository.getUserRanking(userId, ruleId)
            if (stats == null) {
                return@run failure(UserRankingError.UserStatsNotFound)
            } else {
                return@run success(stats)
            }
        }

    /**
     * Searches in the ranking by their username and a specific rule.
     * @param ruleId The id of the rule.
     * @param username The username of the users.
     * @param offset The offset of the first user to get.
     * @param limit The maximum number of users to get.
     * @return A list of [UserStats] objects, containing all the stats related to the users, or null if the ruleId is invalid.
     */
    fun searchRanking(ruleId: Int, username: String?, offset: Int? = null, limit: Int? = null): RankingSearchResult =
        transactionManager.run {
            val availableRules = it.gameRepository.getAllRules()
            if (availableRules.any { rule -> rule.ruleId == ruleId }) {
                val totalCount = it.usersRepository.countRankingEntries(ruleId, username ?: "")
                val users = it.usersRepository.searchRanking(
                    ruleId,
                    username ?: "",
                    offset ?: DEFAULT_OFFSET,
                    limit ?: DEFAULT_LIMIT
                )
                return@run Pair(users, totalCount)
            } else {
                null
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

    /**
     * Creates a token for a user with the [userUUID].
     *
     * @param userUUID user identifier
     * @param tokenValue the value to match the token validation info
     * @return The new token validated.
     */
    private fun createToken(userUUID: Int, tokenValue: String): Token {
        val now = clock.now()
        return Token(
            tokenValidationInfo = usersDomain.createTokenValidationInfo(tokenValue),
            userId = userUUID,
            createdAt = now,
            lastUsedAt = now
        )
    }

    companion object {
        const val DEFAULT_OFFSET = 0
        const val DEFAULT_LIMIT = 10
    }
}
