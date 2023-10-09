package gomoku.server.domain.game.match

enum class MatchState {
    WAITING_PLAYER,
    ONGOING,
    FINISHED
}

fun String.toMatchState(): MatchState {
    return when (this) {
        "WAITING_PLAYER" -> MatchState.WAITING_PLAYER
        "ONGOING" -> MatchState.ONGOING
        "FINISHED" -> MatchState.FINISHED
        else -> throw IllegalArgumentException("Unknown match state: $this")
    }
}
