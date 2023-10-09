package gomoku.server.domain.user

import gomoku.server.domain.game.player.PlayerRuleStats

/**
 * Represents a user with its statistics listed by rules
 * @property uuid unique identifier of the user
 * @property username username of the user
 * @property playerRuleStats statistics of the user listed by rules
 */
data class UserData(
    val uuid: Int,
    val username: String,
    val playerRuleStats: List<PlayerRuleStats>
)
