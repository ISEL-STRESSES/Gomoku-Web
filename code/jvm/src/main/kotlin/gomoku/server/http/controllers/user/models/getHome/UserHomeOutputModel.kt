package gomoku.server.http.controllers.user.models.getHome

/**
 * Represents a user with its statistics listed by rules
 * to be sent from the API
 * @property id unique identifier of the user
 * @property username username of the user
 */
data class UserHomeOutputModel(
    val id: Int,
    val username: String
)
