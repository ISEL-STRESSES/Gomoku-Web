package gomoku.server.domain.user

import gomoku.utils.sha256

/**
 * Represents an encoded token
 */
data class Token(val encodedToken: String) {

    /**
     * Checks if the token is valid
     */
    init {
        check(encodedToken.isNotEmpty()) { "Encoded Token is empty!" }
    }

    companion object {

        /**
         * Encodes a token
         *
         * @param token The token to encode
         * @return Encoded token
         */
        fun encode(token: String): Token {
            check(token.isNotEmpty()) { "Token is empty!" }
            val hashedToken = sha256(token)
            return Token(hashedToken)
        }
    }
}