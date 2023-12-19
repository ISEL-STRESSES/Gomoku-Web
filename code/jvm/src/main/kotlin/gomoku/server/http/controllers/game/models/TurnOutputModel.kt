package gomoku.server.http.controllers.game.models

/**
 * Represents the turn to be sent from the API
 * @param turn the turn
 */
data class TurnOutputModel(
    val turn: Int
)
