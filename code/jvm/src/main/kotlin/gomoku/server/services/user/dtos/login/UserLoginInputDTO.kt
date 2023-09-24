package gomoku.server.services.user.dtos.login

import gomoku.server.domain.user.Password

data class UserLoginInputDTO(
    val username: String,
    val password: String
)
