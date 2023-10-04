package gomoku.server.domain.user

class AuthenticatedUser(
    val user: User,
    val token: String,
)
