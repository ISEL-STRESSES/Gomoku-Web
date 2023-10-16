package gomoku.server.repository.game

import gomoku.server.domain.game.match.Match
import gomoku.server.domain.game.match.MatchOutcome
import gomoku.server.domain.game.match.MatchState
import gomoku.server.domain.game.player.Color
import gomoku.server.domain.game.player.Move
import gomoku.server.domain.game.player.Player
import gomoku.server.domain.game.rules.Rules

/**
 * Repository for match data.
 */
interface MatchRepository {

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

    // Match
    /**
     * Creates a new match, with the given rule and user id
     * setting the match state to [MatchState.WAITING_PLAYER]
     * @param ruleId id of the rule
     * @param playerAId id of the playerA
     * @param playerBId id of the playerB
     * @return id of the match
     */
    fun createMatch(ruleId: Int, playerAId: Int, playerBId: Int): Int

    /**
     * Gets the match by its id.
     * @param matchId id of the match
     * @return the match or null if not found
     */
    fun getMatchById(matchId: Int): Match?

    /**
     * Gets the state of the match.
     * @param matchId id of the match
     * @return state of the match
     */
    fun getMatchState(matchId: Int): MatchState

    /**
     * Sets the state of the match.
     * @param matchId id of the match
     * @param state state of the match
     * @return true if the state was set, false otherwise
     */
    fun setMatchState(matchId: Int, state: MatchState)

    /**
     * Gets the winner of the match.
     * @param matchId id of the match
     * @return outcome of the match or null if the match is not finished
     */
    fun getMatchOutcome(matchId: Int): MatchOutcome?

    /**
     * Sets the [MatchOutcome] of the match.
     * @param matchId id of the match
     * @param outcome outcome of the match
     */
    fun setMatchOutcome(matchId: Int, outcome: MatchOutcome)

    /**
     * Gets the rule of the match.
     * @param matchId id of the match
     * @return the rule
     */
    fun getMatchRule(matchId: Int): Rules

    /**
     * Gets the players of a match.
     * @param matchId id of the match
     * @return pair of players
     */
    fun getMatchPlayers(matchId: Int): Pair<Player, Player>?

    // moves
    /**
     * Makes a move in the match.
     * @param matchId id of the match
     * @param move the position and color of the move
     */
    fun makeMove(matchId: Int, move: Move)

    /**
     * Gets the moves of the match.
     * @param matchId id of the match
     * @return list of moves
     */
    fun getAllMoves(matchId: Int): List<Move>

    /**
     * Gets the last n moves of the match.
     * @param matchId id of the match
     * @param n number of moves to get
     * @return list of moves
     */
    fun getLastNMoves(matchId: Int, n: Int): List<Move>

    /**
     * Gets the turn of the match.
     * @param matchId id of the match
     * @return the id of the player for the current turn.
     */
    fun getTurn(matchId: Int): Color
}
