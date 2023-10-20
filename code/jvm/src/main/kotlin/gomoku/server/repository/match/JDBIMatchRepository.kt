package gomoku.server.repository.match

import gomoku.server.domain.game.match.Color
import gomoku.server.domain.game.match.FinishedMatch
import gomoku.server.domain.game.match.Match
import gomoku.server.domain.game.match.MatchOutcome
import gomoku.server.domain.game.match.MatchState
import gomoku.server.domain.game.match.Move
import gomoku.server.domain.game.match.toColor
import gomoku.server.domain.game.rules.Rules
import gomoku.server.domain.game.rules.RulesRepresentation
import gomoku.server.repository.jdbi.mappers.MovesRowMapper
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo

/**
 * Repository for matches using JDBI and PostgresSQL
 * @property handle The handle to the database
 * @see MatchRepository
 */
class JDBIMatchRepository(private val handle: Handle) : MatchRepository {

    // rules
    /**
     * Gets a rule by its id.
     * @param ruleId id of the rule
     * @return the rule or null if not found
     */
    override fun getRuleById(ruleId: Int): Rules? =
        handle.createQuery("select * from rules where id = :ruleId")
            .bind("ruleId", ruleId)
            .mapTo<Rules>()
            .singleOrNull()

    /**
     * Gets all the rules.
     * @return list of rules
     */
    override fun getAllRules(): List<RulesRepresentation> =
        handle.createQuery("select * from rules")
            .mapTo<RulesRepresentation>()
            .list()

    /**
     * Checks if a rule with the given id is stored in the database.
     * @param ruleId id of the rule
     * @return true if the rule is stored, false otherwise
     */
    override fun isRuleStoredById(ruleId: Int): Boolean =
        handle.createQuery("select id from rules where id = :ruleId")
            .bind("ruleId", ruleId)
            .mapTo<Int>()
            .singleOrNull() != null

    // match
    /**
     * Creates a new match, with the given rule and users ids
     * setting the match state to [MatchState.ONGOING]
     * @param ruleId id of the rule
     * @param playerBlackId id of the player playing with black stones
     * @param playerWhiteId id of the player playing with white stones
     * @return id of the match
     */
    override fun createMatch(ruleId: Int, playerBlackId: Int, playerWhiteId: Int): Int =
        handle.createUpdate(
            """
            insert into matches(rules_id, match_state, player_black, player_white)
            values (:ruleId, :matchState, :playerBlackId, :playerWhiteId)
            """.trimIndent()
        )
            .bind("ruleId", ruleId)
            .bind("matchState", MatchState.ONGOING.name)
            .bind("playerBlackId", playerBlackId)
            .bind("playerWhiteId", playerWhiteId)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .one()

    /**
     * Gets the match by its id.
     * @param matchId id of the match
     * @return the match or null if not found
     */
    override fun getMatchById(matchId: Int): Match? =
        handle.createQuery(
            """
            select matches.id as match_id, matches.player_black, matches.player_white, matches.match_state, matches.match_outcome, matches.moves,
            rules.id as rules_id,rules.board_size, rules.opening_rule, rules.variant
            from matches join rules
            on rules.id = matches.rules_id
            where matches.id = :matchId
            """.trimIndent()
        )
            .bind("matchId", matchId)
            .mapTo<Match>()
            .singleOrNull()

    override fun getUserFinishedMatches(offset: Int, limit: Int, userId: Int): List<FinishedMatch> =
        handle.createQuery(
            """
                select matches.id, matches.player_black, matches.player_white, matches.match_state, matches.match_outcome, matches.moves,
                rules.id as rules_id, rules.board_size, rules.opening_rule, rules.variant
                from matches join rules
                on rules.id = matches.rules_id
                where (matches.player_black = :userId or matches.player_white = :userId) and matches.match_state = 'FINISHED'
                order by matches.id desc
                limit :limit offset :offset
            """.trimIndent()
        )
            .bind("userId", userId)
            .bind("limit", limit)
            .bind("offset", offset)
            .mapTo<FinishedMatch>()
            .list()

    /**
     * Gets the state of the match.
     * @param matchId id of the match
     * @return state of the match or null if the match doesn't exist
     */
    override fun getMatchState(matchId: Int): MatchState? =
        handle.createQuery("select match_state from matches where id = :matchId")
            .bind("matchId", matchId)
            .mapTo<MatchState>()
            .singleOrNull()

    /**
     * Sets the state of the match.
     * @param matchId id of the match
     * @param state the new state of the match
     */
    override fun setMatchState(matchId: Int, state: MatchState) {
        handle.createUpdate(
            """
            update matches set match_state = :state where id = :matchId
            """.trimIndent()
        )
            .bind("matchId", matchId)
            .bind("state", state.name)
            .execute()
    }

    /**
     * Gets the winner of the match.
     * @param matchId id of the match
     * @return id of the winner, draw or null if the match is not finished
     */
    override fun getMatchOutcome(matchId: Int): MatchOutcome? =
        handle.createQuery("select match_outcome from matches where id = :matchId and match_state = 'FINISHED'")
            .bind("matchId", matchId)
            .mapTo<MatchOutcome>()
            .singleOrNull()

    /**
     * Sets the [MatchOutcome] of the match.
     * @param matchId id of the match
     * @param outcome outcome of the match
     */
    override fun setMatchOutcome(matchId: Int, outcome: MatchOutcome) {
        handle.createUpdate(
            """
            update matches set match_outcome = :outcome where id = :matchId and match_state = 'FINISHED'
            """.trimIndent()
        )
            .bind("matchId", matchId)
            .bind("outcome", outcome.name)
            .execute()
    }

    /**
     * Gets the rule of the match.
     * @param matchId id of the match
     * @return the rule or null if the match doesn't exist
     */
    override fun getMatchRule(matchId: Int): Rules? =
        handle.createQuery(
            """
            select id, board_size, variant, opening_rule from rules where rules.id = (
            select rules_id from matches where matches.id = :matchId
            )
            """.trimIndent()
        )
            .bind("matchId", matchId)
            .mapTo<Rules>()
            .singleOrNull()

    /**
     * Gets the players of a match.
     * @param matchId id of the match
     * @return pair of players
     */
    override fun getMatchPlayers(matchId: Int): GamePlayers? =
        handle.createQuery(
            """
            select player_black, player_white from matches where id = :matchId
            """.trimIndent()
        )
            .bind("matchId", matchId)
            .map { rs, _ ->
                Pair(rs.getInt("player_black"), rs.getInt("player_white"))
            }
            .firstOrNull()

    /**
     * Makes a move in the match.
     * @param matchId id of the match
     * @param index index of the move
     * @return true if the move was added, false otherwise
     */
    override fun addToMoveArray(matchId: Int, index: Int): Boolean {
        val update = handle.createUpdate(
            """
            update matches set moves = array_append(matches.moves, :index) where id = :matchId and match_state = 'ONGOING'
            """.trimIndent()
        )
            .bind("matchId", matchId)
            .bind("index", index)
            .execute()

        return update == 1
    }

    /**
     * Gets the moves of the match.
     * @param matchId id of the match
     * @return list of moves
     */
    override fun getAllMoves(matchId: Int): List<Move> =
        handle.createQuery(
            """
            select m.moves, r.board_size from matches m join rules r on r.id = m.rules_id where m.id = :matchId
            """.trimIndent()
        )
            .bind("matchId", matchId)
            .map(MovesRowMapper())
            .one()

    /**
     * Gets the last n moves of the match.
     * @param matchId id of the match
     * @param n number of moves to get
     * @return list of moves
     */
    override fun getLastNMoves(matchId: Int, n: Int): List<Move> =
        getAllMoves(matchId).takeLast(n)

    /**
     * Gets the turn of the match.
     * @param matchId id of the match
     * @return the color of the player whose turn it is, of null if
     * the match has already ended or doesn't exist.
     */
    override fun getTurn(matchId: Int): Color? =
        handle.createQuery(
            """
            SELECT COALESCE(array_length(matches.moves, 1), 0) as array_length
            FROM matches
            WHERE id = :matchId AND match_state = 'ONGOING';
            """.trimIndent()
        )
            .bind("matchId", matchId)
            .mapTo<Int>()
            .singleOrNull()?.toColor()
}
