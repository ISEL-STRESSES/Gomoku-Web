package gomoku.server.domain.user

/**
 * Represents a user
 * @property uuid The id of the user
 * @property username The username of the user
 * @property passwordValidationInfo The password validation info of the user
 */
data class User(
    val uuid: Int,
    val username: String,
    val passwordValidationInfo: PasswordValidationInfo
) {
    companion object {
        const val MIN_PASSWORD_SIZE = 8
        const val MAX_PASSWORD_SIZE = 20
        const val MIN_NAME_SIZE = 3
        const val MAX_NAME_SIZE = 20
    }
}
