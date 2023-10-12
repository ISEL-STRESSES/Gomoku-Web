package gomoku.server.repository.user

import gomoku.server.domain.user.PasswordValidationInfo
import gomoku.server.domain.user.Token
import gomoku.server.domain.user.TokenValidationInfo
import gomoku.server.domain.user.User
import gomoku.server.domain.user.UserData
import kotlinx.datetime.Instant

/**
 * Repository for user operations.
 * Contains methods for storing and retrieving user data.
 */
interface UserRepository {
    /**
     * Stores a user in the database.
     * @param username The username of the user.
     * @param passwordValidationInfo The password validation information of the user.
     * @return The id of the user.
     */
    fun storeUser(username: String, passwordValidationInfo: PasswordValidationInfo): Int

    /**
     * Retrieves a user by their username.
     * @param username The username of the user.
     * @return The user if found, null otherwise.
     */
    fun getUserByUsername(username: String): User?

    /**
     * Checks if a user with the given username is stored in the database.
     * @param username The username of the user.
     * @return True if the user is stored, false otherwise.
     */
    fun isUserStoredByUsername(username: String): Boolean

    /**
     * Retrieves a list of users.
     * @param offset The offset of the users to retrieve.
     * @param limit The maximum number of users to retrieve.
     * @return The list of users.
     */
    fun getUsersStatsData(offset: Int, limit: Int): List<UserData>

    //TODO: MAKE
    //fun getUserStatsByRule(userId: Int, ruleId: Int): UserRuleStats?

    //fun setUserRuleStats(userId: Int, userStatsData: UserRuleStats)

    /**
     * Retrieves a user by their token information.
     * @param tokenValidationInfo The token of the user.
     * @return The user if found, null otherwise.
     */
    fun getTokenAndUserByTokenValidationInfo(tokenValidationInfo: TokenValidationInfo): Pair<User, Token>?

    /**
     * Creates a token for a user.
     * @param token The token to create.
     * @param maxTokens The maximum number of tokens per user.
     */
    fun createToken(token: Token, maxTokens: Int)

    /**
     * Updates the last used time of a token.
     * @param token The token to update.
     * @param now The current time.
     */
    fun updateTokenLastUsed(token: Token, now: Instant)

    /**
     * Removes a token by its token information.
     * @param tokenValidationInfo The token information of the token to remove.
     * @return The number of tokens removed.
     */
    fun removeTokenByTokenValidationInfo(tokenValidationInfo: TokenValidationInfo): Int

    /**
     * Retrieves a user by their id.
     * @param id The id of the user.
     * @return The user if found, null otherwise.
     */
    fun getUserById(id: Int): UserData?

    //TODO Check
    /**
     * Retrieves a list of users with the given username.
     * @param username The username of the users.
     * @return The list of users.
     */
    fun searchRankings(username: String): List<UserData>
}
