package gomoku.server.http.controllers.game.models

/**
 * Represents the output model of turn of the game to be sent from the API
 * @property turn id of the player turn.
 */
data class CurrentTurnPlayerOutput(
    val turn: Int
)
