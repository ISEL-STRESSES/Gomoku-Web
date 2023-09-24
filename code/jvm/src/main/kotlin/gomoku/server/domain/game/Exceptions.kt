package gomoku.server.domain.game

sealed class GomokuException : Exception()
class THIS_AINT_YO_GAME_Exception : GomokuException()
class NotYourTurnException : GomokuException()
class IllegalMoveException : GomokuException()
class OutOfBoundsException : GomokuException()
class GameAlreadyOverException : GomokuException()
class CarlosFezEstaParteDoCodigoException : GomokuException()
