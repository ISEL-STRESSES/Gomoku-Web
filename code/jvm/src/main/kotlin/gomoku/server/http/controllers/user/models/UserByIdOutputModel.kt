package gomoku.server.http.controllers.user.models

import gomoku.server.domain.user.PasswordValidationInfo
import gomoku.server.domain.user.User

/**
 * Represents a user with its information
 * to be sent from the API
 * @property uuid unique identifier of the user
 * @property username username of the user
 * @property passwordValidationInfo password validation information of the user
 */
data class UserByIdOutputModel(
    val uuid: Int,
    val username: String,
    val passwordValidationInfo: PasswordValidationInfo
) {
    constructor(user: User) : this(
        uuid = user.uuid,
        username = user.username,
        passwordValidationInfo = user.passwordValidationInfo
    )
}
