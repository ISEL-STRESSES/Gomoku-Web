package gomoku.server.services.user.daos

class UserDetailsDAO(
    id: Int,
    username: String,
    val gamesPlayed: Int,
    val elo: Int,
    val password: String,
): UserDAO(id, username)
