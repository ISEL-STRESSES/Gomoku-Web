package gomoku.server.http.controllers.game.models

/**
 * Represents the input model of a play position in the game
 * @property x the x position of the play
 * @property y the y position of the play
 */
data class PlayPositionInput(
    val x: Int,
    val y: Int
)
