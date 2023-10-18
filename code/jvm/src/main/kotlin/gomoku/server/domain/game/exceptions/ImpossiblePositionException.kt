package gomoku.server.domain.game.exceptions

/**
 * Represents an impossible position exception.
 * @param msg The exception message
 * @see Exception
 */
class ImpossiblePositionException(msg :String = "") : Exception(msg)