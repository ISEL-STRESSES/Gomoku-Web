package gomoku.server.repository.game

import gomoku.server.domain.game.Rules
import gomoku.server.domain.game.board.Position

/**
 * Repository for game data.
 */
interface MatchRepository {

    /**
     * Creates a new set of rules.
     * @param rules rules of the game
     * @return id of the lobby
     */
    fun createGameRules(rules: Rules): Int

    /**
     * Creates a new waiting lobby.
     * @param rules rules of the game
     * @return id of the lobby
     */
    fun joinWaitingLobby(rules: Rules): Int

    /**
     * Initiates a game between two players.
     * @param playerAId id of the first player
     * @param playerBId id of the second player
     * @return id of the game
     */
    fun initiateGame(playerAId: Int, playerBId: Int): Int

    /**
     * Gets the winner of the game.
     * @param gameId id of the game
     * @return id of the winner, draw or null if the game is not finished
     */
    fun getWinner(gameId: Int): String?

    /**
     * Gets the moves of the game.
     * @param gameId id of the game
     * @return list of moves
     */
    fun getMoves(gameId: Int): List<Position>?

    /**
     * Makes a move in the game.
     * @param gameId id of the game
     * @param position position of the move
     * @param matchId id of the match
     */
    fun makeMove(gameId: Int, position: Position, matchId: Int)

    /**
     * Gets the state of the game.
     * @param gameId id of the game
     * @return state of the game
     */
    fun getGameState(gameId: Int) // TODO: return GameState

    /**
     * Gets the turn of the game.
     * @param gameId id of the game
     * @return the id of the player for the current turn.
     */
    fun getTurn(gameId: Int): Int?
}
