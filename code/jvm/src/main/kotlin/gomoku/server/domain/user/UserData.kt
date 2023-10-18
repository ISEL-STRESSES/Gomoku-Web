package gomoku.server.domain.user

/**
 * Represents a user with its statistics listed by rules
 * @property uuid unique identifier of the user
 * @property username username of the user
 * @property userRuleStats statistics of the user listed by rules
 */
data class UserData(
    val uuid: Int,
    val username: String,
    val userRuleStats: List<UserRuleStats>
)

/**
 * TODO check why 2 classes for the same thing
 */
data class ListUserData(
    val uuid: Int,
    val username: String,
    val userRuleStats: List<UserRuleStats>
)
