package gomoku.server.http.controllers.user.models.getUsersData

import gomoku.server.http.controllers.user.models.UserDataOutputModel

data class GetUsersDataOutputModel(
    val userData: List<UserDataOutputModel>
)
