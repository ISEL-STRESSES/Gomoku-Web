package gomoku.server.domain.game

enum class MatchState {
    WAITING_FOR_PLAYER,
    ONGOING,
    FINISHED
}

fun String.toMatchState(): MatchState {
    return when (this) {
        "WAITING_FOR_PLAYER" -> MatchState.WAITING_FOR_PLAYER
        "ONGOING" -> MatchState.ONGOING
        "FINISHED" -> MatchState.FINISHED
        else -> throw IllegalArgumentException("Unknown match state: $this")
    }
}
