package gomoku.server.http.controllers.user.models.register

import gomoku.server.domain.User
import gomoku.server.services.user.dtos.register.UserRegisterInputDTO
import gomoku.server.validation.SafePassword
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UserRegisterInputModel(

    @NotBlank
    @get:Size(
        min = User.MIN_NAME_SIZE,
        max = User.MAX_NAME_SIZE
    )
    val username: String,

    @field:Email
    val email: String,

    @SafePassword
    val password: String
){
    fun toUserRegisterInputDTO() = UserRegisterInputDTO(
        username = username,
        email = email,
        password = password
    )
}