package gomoku.server.http.controllers.user.models.get

import gomoku.server.services.user.dtos.get.UserDetailOutputDTO

data class UserDetailOutputModel(
    val uuid: Int,
    val username: String,
    val playCount: Int,
    val elo: Int
) {
    constructor(userDetailOutputDTO: UserDetailOutputDTO) : this(
        uuid = userDetailOutputDTO.uuid,
        username = userDetailOutputDTO.username,
        playCount = userDetailOutputDTO.playCount,
        elo = userDetailOutputDTO.elo
    )
}
