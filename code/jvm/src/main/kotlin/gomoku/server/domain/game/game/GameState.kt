package gomoku.server.domain.game.game

/**
 * Represents the state of the game
 * @property ONGOING the game is ongoing
 * @property FINISHED the game is finished
 */
enum class GameState {
    ONGOING,
    FINISHED
}

/**
 * Converts a string to a [GameState]
 * @receiver the string to convert
 * @return the [GameState]
 */
fun String.toGameState(): GameState {
    return when (this) {
        "ONGOING" -> GameState.ONGOING
        "FINISHED" -> GameState.FINISHED
        else -> throw IllegalArgumentException("Unknown game state: $this")
    }
}
