package gomoku.server.http.controllers.user.models.userCreate

/**
 * Represents a user created with its id and active token to be sent from the API
 *
 * @property userId user identifier
 * @property token active token
 */
data class UserCreateOutputModel(
    val userId: Int,
    val username: String,
    val token: String,
    val tokenExpiration: String
)
