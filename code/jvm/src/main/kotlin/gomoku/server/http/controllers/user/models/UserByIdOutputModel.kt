package gomoku.server.http.controllers.user.models

import gomoku.server.domain.user.PasswordValidationInfo
import gomoku.server.domain.user.User

data class UserByIdOutputModel(
    val uuid: Int,
    val username: String,
    val passwordValidationInfo: PasswordValidationInfo
){
    constructor(user: User) : this(
        uuid = user.uuid,
        username = user.username,
        passwordValidationInfo = user.passwordValidationInfo
    )
}