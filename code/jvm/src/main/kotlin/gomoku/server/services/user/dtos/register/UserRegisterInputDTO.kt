package gomoku.server.services.user.dtos.register

data class UserRegisterInputDTO(
    val username: String,
    val email: String,
    val password: String
)