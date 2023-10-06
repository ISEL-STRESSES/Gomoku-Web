package gomoku.server.domain.user

data class UserData(
    val uuid: Int,
    val username: String,
    val playCount: Int = 0,
    val elo: Int = 0
)
