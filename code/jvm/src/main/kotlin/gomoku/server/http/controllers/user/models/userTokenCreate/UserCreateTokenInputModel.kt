package gomoku.server.http.controllers.user.models.userTokenCreate

/**
 * Represents the input model for creating a user token
 * sent to the API
 * @property username The username of the user
 * @property password The password of the user
 * @property sendTokenViaCookie Whether the token should be sent via cookie or body
 */
data class UserCreateTokenInputModel(

    val username: String,

    val password: String,

    val sendTokenViaCookie: Boolean = false
)
