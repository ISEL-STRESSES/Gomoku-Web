package gomoku.server.http.controllers.user.models.login

import gomoku.server.domain.user.UserDetails
import gomoku.server.services.user.dtos.login.UserLoginInputDTO
import gomoku.server.validation.SafePassword
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UserLoginInputModel(

    @NotBlank
    @get:Size(
        min = UserDetails.MIN_NAME_SIZE,
        max = UserDetails.MAX_NAME_SIZE
    )
    val username: String,

    @SafePassword
    val password: String
) {
    fun toUserLoginInputDTO() = UserLoginInputDTO(
        username = username,
        password = password
    )
}