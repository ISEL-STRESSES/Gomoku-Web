package gomoku.server.repository.game

import gomoku.server.domain.game.MatchOutcome
import gomoku.server.domain.game.MatchState
import gomoku.server.domain.game.player.Color
import gomoku.server.domain.game.player.Move
import gomoku.server.domain.game.rules.Rule

/**
 * Repository for match data.
 */
interface MatchRepository {

    /**
     * Gets the id of a set of rules, if not found it creates a new one.
     * @param rule rules of the match
     * @return id of the rule
     */
    fun getRuleId(rule: Rule): Int

    /**
     * Initiates a match between two players.
     * @param playerAId id of the first player
     * @param playerBId id of the second player
     * @return id of the match
     */
    fun initiateMatch(playerAId: Int, playerBId: Int): Int

    /**
     * Gets the winner of the match.
     * @param matchId id of the match
     * @return id of the winner, draw or null if the match is not finished
     */
    fun getWinner(matchId: Int): MatchOutcome?

    /**
     * Gets the moves of the match.
     * @param matchId id of the match
     * @return list of moves
     */
    fun getMoves(matchId: Int, rule: Rule): List<Move>

    /**
     * Makes a move in the match.
     * @param matchId id of the match
     * @param move the position and color of the move
     */
    fun makeMove(matchId: Int, rule: Rule, move: Move)

    /**
     * Gets the state of the match.
     * @param matchId id of the match
     * @return state of the match
     */
    fun getMatchState(matchId: Int): MatchState // TODO: return matchState

    /**
     * Gets the turn of the match.
     * @param matchId id of the match
     * @return the id of the player for the current turn.
     */
    fun getTurn(matchId: Int): Color
}
