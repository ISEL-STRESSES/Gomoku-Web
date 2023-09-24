package gomoku.server.domain.user

/**
 * Represents an encoded token
 */
data class Password(val encodedPassword: String) {

    /**
     * Checks if the password is valid
     */
    init {
        check(encodedPassword.isNotEmpty()) { "Encoded gomoku.server.domain.user.Password is empty!" }
    }
}