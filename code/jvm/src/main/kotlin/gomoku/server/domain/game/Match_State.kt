package gomoku.server.domain.game

enum class Match_State {
    WAITING_FOR_PLAYER,
    ONGOING,
    FINISHED
}

fun String.toMatchState(): Match_State {
    return when (this) {
        "WAITING_FOR_PLAYER" -> Match_State.WAITING_FOR_PLAYER
        "ONGOING" -> Match_State.ONGOING
        "FINISHED" -> Match_State.FINISHED
        else -> throw IllegalArgumentException("Unknown match state: $this")
    }
}