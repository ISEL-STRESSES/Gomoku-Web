package gomoku.server.http.controllers.user.models.userTokenCreate

/**
 * Represents the output model for creating a user token
 * sent from the API
 * @property token The token of the user
 */
data class UserTokenCreateOutputModel(
    val token: String
)
