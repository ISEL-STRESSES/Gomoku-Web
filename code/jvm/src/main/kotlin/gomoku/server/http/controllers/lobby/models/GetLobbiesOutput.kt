package gomoku.server.http.controllers.lobby.models

import gomoku.server.domain.game.Lobby

data class GetLobbiesOutput(
    val lobbies: List<Lobby>
)