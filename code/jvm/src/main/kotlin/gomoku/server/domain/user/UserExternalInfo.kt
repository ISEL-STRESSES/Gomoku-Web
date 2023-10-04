package gomoku.server.domain.user

data class UserExternalInfo(
    val uuid: Int,
    val username: String,
    val playCount: Int = 0,
    val elo: Int = 0,
)
