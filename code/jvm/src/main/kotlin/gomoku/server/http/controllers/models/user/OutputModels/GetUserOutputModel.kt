package gomoku.server.http.controllers.models.user.OutputModels

import gomoku.server.domain.user.User

data class GetUserOutputModel(
    val user: UserDetailOutputModel
) {
    constructor(user: User) : this(
        user = UserDetailOutputModel(user)
    )
}
