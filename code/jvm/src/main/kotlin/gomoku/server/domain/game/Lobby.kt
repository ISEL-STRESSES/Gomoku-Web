package gomoku.server.domain.game

import gomoku.server.domain.game.rules.Rules

/**
 * Represents a lobby
 * @property id The id of the lobby
 * @property rule The rule of the lobby
 * @property userId The id of the first user to join the lobby
 */
data class Lobby(
    val id: Int,
    val rule: Rules,
    val userId: Int,
    val state: Boolean,
    val gameId: Int
)
