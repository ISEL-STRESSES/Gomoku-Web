package gomoku.server.http.model

data class CreateUserResponse(
    val userId: Int,
    val token: String
)
