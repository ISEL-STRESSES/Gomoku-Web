package gomoku.server.repository.user

import gomoku.server.domain.user.PasswordValidationInfo
import gomoku.server.domain.user.RankingUserData
import gomoku.server.domain.user.Token
import gomoku.server.domain.user.TokenValidationInfo
import gomoku.server.domain.user.User
import gomoku.server.domain.user.UserStats
import kotlinx.datetime.Instant

/**
 * Repository for user operations.
 * Contains methods for storing and retrieving user data.
 */
interface UserRepository {
    // user
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
     * Checks if a user with the given id is stored in the database.
     * @param id The id of the user.
     * @return True if the user is stored, false otherwise.
     */
    fun isUserStoredById(id: Int): Boolean

    /**
     * Retrieves a user by their id.
     * @param id The id of the user.
     * @return The user if found, null otherwise.
     */
    fun getUserById(id: Int): User?

    // token
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

    // stats
    /**
     * Retrieves the stats of a user for every rule.
     * @param userId The id of the user.
     * @return The stats of the user for every rule, or null if the user doesn't exist.
     */
    fun getUserStats(userId: Int): UserStats?

    /**
     * Retrieves the stats of a user for a given rule.
     * @param userId The id of the user.
     * @param ruleId The id of the rule.
     * @return The stats of the user for the given rule,
     * or null if the user never played on this setting.
     */
    fun getUserRanking(userId: Int, ruleId: Int): RankingUserData?

    /**
     * Sets the stats of a user for a given rule.
     * @param userId The id of the user.
     * @param rankingUserData The stats of the user for the given rule.
     */
    fun setUserRanking(userId: Int, rankingUserData: RankingUserData)

    /**
     * Retrieves a list of users with the given username.
     * @param rulesId The id of the rules.
     * @param username The username of the users.
     * @param offset The offset of the first user to get.
     * @param limit The maximum number of users to get.
     * @return The list of users.
     */
    fun searchRanking(rulesId: Int, username: String, offset: Int, limit: Int): List<RankingUserData>

    /**
     * Counts the number of entries in the ranking.
     * @param rulesId The id of the rules.
     * @param username The username of the users.
     * @return The number of entries in the ranking.
     */
    fun countRankingEntries(rulesId: Int, username: String): Int
}
