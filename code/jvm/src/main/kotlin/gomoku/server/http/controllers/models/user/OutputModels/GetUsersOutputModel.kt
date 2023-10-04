package gomoku.server.http.controllers.models.user.OutputModels

import gomoku.server.domain.user.UserExternalInfo

data class GetUsersOutputModel(
    val userDetails: List<UserExternalInfo>
)
