package gomoku.server.http.controllers.models.user.InputModels

import gomoku.server.domain.user.User
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
)