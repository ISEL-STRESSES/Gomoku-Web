package gomoku.server.domain.user

/**
 * Represents an encoded token
 */
data class Token(val encodedToken: String) {

    /**
     * Checks if the token is valid
     */
    init {
        check(encodedToken.isNotBlank()) { "Encoded Token is empty!" }
    }

    companion object {
        const val DEFAULT_TTL = 3600
    }
}
