package gomoku.server.http.controllers.user.models.get

import gomoku.server.services.user.dtos.get.GetUserOutputDTO

data class GetUserOutputModel(
    val user: UserDetailOutputModel
) {
    constructor(getUserOutputDTO: GetUserOutputDTO) : this(
        user = UserDetailOutputModel(getUserOutputDTO.user)
    )
}