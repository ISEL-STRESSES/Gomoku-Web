package gomoku.server.http.controllers.user.models.userCreate

/**
 * Represents the input model for creating a user
 * sent to the API
 * @property username The username of the user
 * @property password The password of the user
 * @property sendTokenViaCookie Whether to send the token via cookie or body
 */
data class UserCreateInputModel(

    val username: String,

    val password: String,

    val sendTokenViaCookie: Boolean = false
)
