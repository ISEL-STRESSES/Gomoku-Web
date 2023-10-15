package gomoku.server.domain.game

/**
 * Represents the result of a start matchmaking request
 * @property isMatch if the matchmaking was successful,
 * meaning that a match was created,
 * or if not, meaning that the user was added to the lobby
 * @property id the id of the match or lobby
 */
data class Matchmaker(
    val isMatch: Boolean,
    val id: Int
)
