package gomoku.server.http.controllers.user.models.get

import gomoku.server.services.user.dtos.get.GetUsersOutputDTO

data class GetUsersOutputModel(
    val users: List<UserDetailOutputModel>
) {
    constructor(getUsersOutputDTO: GetUsersOutputDTO) : this(
        users = getUsersOutputDTO.users.map { UserDetailOutputModel(it) }
    )
}
