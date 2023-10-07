package gomoku.server.domain.user

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
