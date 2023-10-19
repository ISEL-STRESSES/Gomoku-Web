package gomoku.server.http.controllers.user.models.userCreate

import gomoku.server.domain.user.User
import gomoku.server.validation.SafePassword
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

/**
 * Represents the input model for creating a user
 * sent to the API
 * @property username The username of the user
 * @property password The password of the user
 */
data class UserCreateInputModel(

    @NotBlank
    @get:Size(
        min = User.MIN_NAME_SIZE,
        max = User.MAX_NAME_SIZE
    )
    val username: String,

    @SafePassword
    val password: String
)
