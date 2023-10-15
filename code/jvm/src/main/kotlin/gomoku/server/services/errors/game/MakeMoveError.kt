package gomoku.server.services.errors.game

sealed class MakeMoveError {
    object NotYourTurn : MakeMoveError()
    object InvalidMove : MakeMoveError() //TODO dizer porque? tipo "invalid move, already occupied", "invalid move, out of bounds", etc
}