package gomoku.server.domain.game

import gomoku.server.domain.game.rules.Rules

/**
 * Represents a lobby
 * @property rule The rule of the lobby
 * @property userId The id of the first user to join the lobby
 */
data class Lobby(
    val rule: Rules,
    val userId: Int
)
