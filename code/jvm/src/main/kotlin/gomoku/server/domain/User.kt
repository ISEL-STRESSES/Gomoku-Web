package gomoku.server.domain

data class User(

    val uuid: Int,
    val username: String,
    val email: String,
    val playCount: Int = 0,
    val elo: Int = 0,

    ) {

    init {
        check(isNameValid(username)) { "User name is invalid!" }
        check(isEmailValid(email)) { "User email is invalid!" }
    }

    companion object {
        const val MIN_NAME_SIZE = 3
        const val MAX_NAME_SIZE = 20
        const val MIN_PASSWORD_SIZE = 8
        const val MAX_PASSWORD_SIZE = 20

        private val UserNameRegex = "^[a-zA-Z0-9 ]{$MIN_NAME_SIZE,$MAX_NAME_SIZE}".toRegex()
        private val UserEmailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$".toRegex()

        /**
         * Checks if the given name is valid
         */
        fun isNameValid(name: String): Boolean {
            return UserNameRegex.matches(name)
        }

        /**
         * Checks if the given email is valid
         */
        fun isEmailValid(email: String): Boolean {
            return UserEmailRegex.matches(email)
        }

    }
}