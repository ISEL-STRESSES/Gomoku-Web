package gomoku.server.http.controllers.user.models.getHome

/**
 * Represents a user output model
 * to be sent from the API
 * @property id unique identifier of the user
 * @property username username of the user
 * @property token the token of the user
 */
data class UserHomeOutputModel(
    val id: Int,
    val username: String,
    val token: String
)
