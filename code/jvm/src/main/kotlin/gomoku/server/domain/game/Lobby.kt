package gomoku.server.domain.game

import gomoku.server.domain.game.rules.Rules
import gomoku.server.domain.user.User

/**
 * Represents a lobby
 * @property rule The rule of the lobby
 * @property user1 The first user to join the lobby
 * @property user2 The second user to join the lobby or null if no second user has joined yet
 */
data class Lobby(
    val rule: Rules,
    val user1: User,
    val user2: User? = null
)
