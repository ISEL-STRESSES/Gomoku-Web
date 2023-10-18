package gomoku.server.domain.game.match

/**
 * Represents the state of a match
 * @property ONGOING the match is ongoing
 * @property FINISHED the match is finished
 */
enum class MatchState {
    ONGOING,
    FINISHED
}

/**
 * Converts a string to a [MatchState]
 * @receiver the string to convert
 * @return the [MatchState]
 */
fun String.toMatchState(): MatchState {
    return when (this) {
        "ONGOING" -> MatchState.ONGOING
        "FINISHED" -> MatchState.FINISHED
        else -> throw IllegalArgumentException("Unknown match state: $this")
    }
}
