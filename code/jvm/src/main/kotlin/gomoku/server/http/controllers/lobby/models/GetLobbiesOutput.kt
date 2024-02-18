package gomoku.server.http.controllers.lobby.models

import gomoku.server.domain.game.Lobby

/**
 * Represents the output model of all lobbies to be sent from the API
 * @property lobbies list of available lobbies.
 */
data class GetLobbiesOutput(
    val lobbies: List<Lobby>
)
