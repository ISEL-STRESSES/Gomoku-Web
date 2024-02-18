package gomoku.server.domain.game.exceptions

/**
 * Thrown when a player tries to play on a position that is already occupied.
 * @param msg the detail message
 * @see Exception
 */
class PositionAlreadyOccupiedException(msg: String = "") : Exception(msg)
