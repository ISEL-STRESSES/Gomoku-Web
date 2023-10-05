package gomoku.server.http.controllers.user.models.userTokenCreate

import gomoku.server.domain.user.User
import gomoku.server.validation.SafePassword
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UserCreateTokenInputModel(

    @NotBlank
    @get:Size(
        min = User.MIN_NAME_SIZE,
        max = User.MAX_NAME_SIZE
    )
    val username: String,

    @SafePassword
    val password: String
)
