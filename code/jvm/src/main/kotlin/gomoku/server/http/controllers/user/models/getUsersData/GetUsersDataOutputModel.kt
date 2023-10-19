package gomoku.server.http.controllers.user.models.getUsersData

import gomoku.server.http.controllers.user.models.UserDataOutputModel

/**
 * Represents a list of users with their statistics
 * to be sent from the API
 * @property userData list of users with their statistics
 */
data class GetUsersDataOutputModel(
    val userData: List<UserDataOutputModel>
)
