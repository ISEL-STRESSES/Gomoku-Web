package gomoku.server.http.controllers.models.user.OutputModels

import gomoku.server.domain.user.User

data class UserDetailOutputModel(
    val uuid: Int,
    val username: String,
    val playCount: Int,
    val elo: Int
) {
    constructor(user: User) : this(
        uuid = user.uuid,
        username = user.username,
        playCount = user.playCount,
        elo = user.elo
    )
}
