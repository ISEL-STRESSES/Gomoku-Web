package gomoku.server.repository.game

import gomoku.server.domain.game.game.CellColor
import gomoku.server.domain.game.game.FinishedGame
import gomoku.server.domain.game.game.Game
import gomoku.server.domain.game.game.GameOutcome
import gomoku.server.domain.game.game.GameState
import gomoku.server.domain.game.game.move.Move
import gomoku.server.domain.game.rules.Rules

typealias GamePlayers = Pair<Int, Int> // black, white

/**
 * Repository for game data.
 */
interface GameRepository {

    // Rules
    /**
     * Gets a rule by its id.
     * @param ruleId id of the rule
     * @return the rule or null if not found
     */
    fun getRuleById(ruleId: Int): Rules?

    /**
     * Gets all the rules.
     * @return list of rules
     */
    fun getAllRules(): List<Rules>

    /**
     * Checks if a rule with the given id is stored in the database.
     * @param ruleId id of the rule
     * @return true if the rule is stored, false otherwise
     */
    fun isRuleStoredById(ruleId: Int): Boolean

    // game
    /**
     * Verifies if the game is stored based on the [gameId]
     * @param gameId id of the game
     * @return true if the game is stored, false otherwise
     */
    fun isGameStoredById(gameId: Int): Boolean

    /**
     * Creates a new game, with the given rule and users ids
     * setting the game state to [GameState.ONGOING]
     * @param ruleId id of the rule
     * @param playerBlackId id of the player playing with black stones
     * @param playerWhiteId id of the player playing with white stones
     * @return id of the game
     */
    fun createGame(ruleId: Int, playerBlackId: Int, playerWhiteId: Int): Int

    /**
     * Gets the game by its id.
     * @param gameId id of the game
     * @return the game or null if not found
     */
    fun getGameById(gameId: Int): Game?

    /**
     * Gets the finished gamees of a user.
     * @param offset the offset of the gamees list
     * @param limit the limit
     */
    fun getUserFinishedGames(offset: Int, limit: Int, userId: Int): List<FinishedGame>

    /**
     * Gets the number of finished games a user has.
     * @param userId the id of the user
     * @return the number of finished games
     */
    fun getUserFinishedGamesCount(userId: Int): Int

    /**
     * Gets the state of the game.
     * @param gameId id of the game
     * @return state of the game or null if the game doesn't exist
     */
    fun getGameState(gameId: Int): GameState?

    /**
     * Sets the state of the game.
     * @param gameId id of the game
     * @param state state of the game
     */
    fun setGameState(gameId: Int, state: GameState)

    /**
     * Gets the winner of the game.
     * @param gameId id of the game
     * @return outcome of the game or null if the game is
     * not finished or doesn't exist
     */
    fun getGameOutcome(gameId: Int): GameOutcome?

    /**
     * Sets the [GameOutcome] of the game.
     * @param gameId id of the game
     * @param outcome outcome of the game
     */
    fun setGameOutcome(gameId: Int, outcome: GameOutcome)

    /**
     * Gets the rule of the game.
     * @param gameId id of the game
     * @return the rule or null if the game doesn't exist
     */
    fun getGameRule(gameId: Int): Rules?

    /**
     * Gets the players of a game.
     * @param gameId id of the game
     * @return the GamePlayers or null if the game doesn't exist
     */
    fun getGamePlayers(gameId: Int): GamePlayers?

    // moves
    /**
     * Makes a move in the game.
     * @param gameId id of the game
     * @param index index of the move
     * @return true if the move was added, false otherwise
     */
    fun addToMoveArray(gameId: Int, index: Int): Boolean

    /**
     * Gets the moves of the game.
     * @param gameId id of the game
     * @return list of moves
     */
    fun getAllMoves(gameId: Int): List<Move>

    /**
     * Gets the last n moves of the game.
     * @param gameId id of the game
     * @param n number of moves to get
     * @return list of moves
     */
    fun getLastNMoves(gameId: Int, n: Int): List<Move>

    /**
     * Gets the turn of the game.
     * @param gameId id of the game
     * @return the color of the player whose turn it is, of null if
     * the game has already ended or doesn't exist.
     */
    fun getTurn(gameId: Int): CellColor?
}
