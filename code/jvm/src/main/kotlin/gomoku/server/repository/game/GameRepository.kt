package gomoku.server.repository.game

interface GameRepository {
    fun createWaitingLobby() :Int
    fun initiateGame()
    fun finishGame()
    fun getWinner()
    fun getMoves(uuid: Int)
    fun makeMove(uuid: Int)
    fun getGameState()
    fun getTurn() :Int
}