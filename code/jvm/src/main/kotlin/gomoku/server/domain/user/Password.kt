import gomoku.utils.sha256

/**
 * Represents an encoded token
 */
data class Password(val encodedPassword: String) {

    /**
     * Checks if the password is valid
     */
    init {
        check(encodedPassword.isNotEmpty()) { "Encoded Password is empty!" }
    }

    companion object {
        /**
         * Encodes a password
         *
         * @param email User's email to be used as hash salt
         * @param password User's password
         * @return Encoded password
         * @throws IllegalStateException If the password is empty
         */
        fun encode(email: String, password: String): Password {
            check(password.isNotEmpty()) { "Password is empty!" }
            val hashedPassword = sha256(email + password)
            return Password(hashedPassword)
        }
    }
}