package gomoku.server.http.controllers.models.user.OutputModels

import gomoku.server.domain.user.User

data class GetUsersOutputModel(
    val userDetails: List<User>
)