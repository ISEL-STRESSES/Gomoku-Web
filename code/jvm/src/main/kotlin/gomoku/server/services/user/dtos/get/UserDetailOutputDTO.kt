package gomoku.server.services.user.dtos.get

data class UserDetailOutputDTO(
    val uuid: Int,
    val username: String,
    val playCount: Int,
    val elo: Int
)
