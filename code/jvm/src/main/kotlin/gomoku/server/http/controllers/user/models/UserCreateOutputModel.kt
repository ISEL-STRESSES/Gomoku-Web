package gomoku.server.http.controllers.user.models

/**
 * Represents a user created with its id and active token to be sent from the API
 *
 * @property userId user identifier
 * @property token active token
 */
data class UserCreateOutputModel(
    val userId: Int,
    val token: String
)
