package gomoku.server.http.controllers.user.models.register

import gomoku.server.services.user.dtos.register.UserRegisterOutputDTO

data class UserRegisterOutputModel(
    val token: String
){
    constructor(userRegisterOutputDTO: UserRegisterOutputDTO) : this(
        token = userRegisterOutputDTO.token
    )
}