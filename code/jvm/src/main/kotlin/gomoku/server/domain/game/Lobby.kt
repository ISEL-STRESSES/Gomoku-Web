package gomoku.server.domain.game

import gomoku.server.domain.game.rules.Rules
import gomoku.server.domain.user.User

/**
 * Represents a lobby
 * @property id The id of the lobby
 * @property rule The rule of the lobby
 * @property players The players in the lobby
 */
data class Lobby(
    val id: Int,
    val rule: Rule,
    val players: List<User> = emptyList()
)
