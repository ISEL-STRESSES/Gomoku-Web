package gomoku.server.domain.game.game

/**
 * Represents a turn in a game
 * @param color The color of the player who made the turn
 * @param user The user who made the turn
 */
data class Turn(val color: CellColor, val user: Int)

/**
 * Helper function to convert a color to a turn
 * @receiver The color of the player who made the turn
 * @param black The id of the user with the black color
 * @param white The id of the user with the white color
 * @return The turn with the user id and corresponding color
 */
fun CellColor.toTurn(black: Int, white: Int) = Turn(this, if (this == CellColor.BLACK) black else white)
