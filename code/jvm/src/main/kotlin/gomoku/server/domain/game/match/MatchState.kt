package gomoku.server.domain.game.match

enum class MatchState {
    ONGOING,
    FINISHED
}

fun String.toMatchState(): MatchState {
    return when (this) {
        "ONGOING" -> MatchState.ONGOING
        "FINISHED" -> MatchState.FINISHED
        else -> throw IllegalArgumentException("Unknown match state: $this")
    }
}
