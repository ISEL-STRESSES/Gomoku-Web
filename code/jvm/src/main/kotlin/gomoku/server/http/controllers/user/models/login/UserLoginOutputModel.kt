package gomoku.server.http.controllers.user.models.login

import gomoku.server.services.user.dtos.login.UserLoginOutputDTO

data class UserLoginOutputModel(
    val token: String
) {
    constructor(userLoginOutputDTO: UserLoginOutputDTO) : this(
        token = userLoginOutputDTO.token
    )
}
