package gomoku.server.domain.game

sealed class GomokuException : Exception()

class InvalidBoardException : GomokuException()
class AlreadyInGameException : GomokuException()
class NotYourTurnException : GomokuException()
class IllegalMoveException : GomokuException()
class OutOfBoundsException : GomokuException()
class GameAlreadyOverException : GomokuException()
class UserNotInGameException : GomokuException()
