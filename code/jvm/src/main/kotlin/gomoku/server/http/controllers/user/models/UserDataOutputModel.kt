package gomoku.server.http.controllers.user.models

import gomoku.server.domain.user.UserData

data class UserDataOutputModel(
    val uuid: Int,
    val username: String,
    val playCount: Int,
    val elo: Int
) {
    constructor(userData: UserData) : this(
        uuid = userData.uuid,
        username = userData.username,
        playCount = userData.playCount,
        elo = userData.elo
    )
}
