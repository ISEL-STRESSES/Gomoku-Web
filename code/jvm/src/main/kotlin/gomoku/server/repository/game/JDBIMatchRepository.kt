package gomoku.server.repository.game

import gomoku.server.domain.game.match.Match
import gomoku.server.domain.game.match.MatchOutcome
import gomoku.server.domain.game.match.MatchState
import gomoku.server.domain.game.match.toMatchState
import gomoku.server.domain.game.player.Color
import gomoku.server.domain.game.player.Move
import gomoku.server.domain.game.player.Player
import gomoku.server.domain.game.player.toColor
import gomoku.server.domain.game.rules.Rules
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.transaction.TransactionIsolationLevel

class JDBIMatchRepository(private val handle: Handle) : MatchRepository {

    /**
     * Gets the id of a set of rules, if not found it creates a new one.
     * @param rules rules of the game
     * @return id of the rule
     */
    override fun getRuleId(rules: Rules): Int? =
        handle.createQuery(
            """
                select id from rules where 
                board_size = :boardSize and 
                opening_rule = :openingRule and 
                variant = :variant
            """.trimIndent()
        )
            .bind("boardSize", rules.boardSize.value)
            .bind("openingRule", rules.openingRule)
            .bind("variant", rules.variant)
            .mapTo<Int>()
            .singleOrNull()

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
    override fun getAllRules(): List<Rules> =
        handle.createQuery("select * from rules")
            .mapTo<Rules>()
            .list()

    /**
     * Creates a new match, with the given rule and user id
     * setting the match state to [MatchState.WAITING_PLAYER]
     * @param ruleId id of the rule
     * @param userId id of the user
     * @return id of the match
     */
    override fun createMatch(ruleId: Int, userId: Int): Int {
        val matchId = handle.createUpdate(
            """
        insert into matches(rules_id, match_state)
        values (:ruleId, :matchState)
            """.trimIndent()
        )
            .bind("ruleId", ruleId)
            .bind("matchState", MatchState.WAITING_PLAYER)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .one()

        handle.createUpdate(
            """
        insert into player(user_id, match_id, rules_id, color)
        values (:userId, :matchId, :rulesId, :color)
            """.trimIndent()
        )
            .bind("userId", userId)
            .bind("matchId", matchId)
            .bind("rulesId", ruleId)
            .bind("color", Color.BLACK)
            .execute()
        return matchId
    }

    /**
     * Joins a player to an already existing match.
     * @param matchId id of the match
     * @param userId id of the user to join
     * @return id of the match
     */
    override fun joinUserToMatch(matchId: Int, userId: Int): Int {
        handle.transactionIsolationLevel = TransactionIsolationLevel.REPEATABLE_READ
        val match = handle.createQuery("select id from matches where id = :matchId")
            .bind("matchId", matchId)
            .mapTo<Int>()
            .singleOrNull() ?: throw IllegalStateException("Match not found")
        handle.createUpdate(
            """
            insert into player(user_id, match_id, rules_id, color)
            values (:userId, :matchId,(select rules_id from matches where match_id = :matchId), 'WHITE')
            """.trimIndent()
        )
            .bind("userId", userId)
            .bind("matchId", matchId)
            .execute()
        return match
    }

    // TODO check this one
    /**
     * Initiates a match between two players.
     * @param playerA the first player
     * @param playerB the second player
     * @return id of the match
     */
    override fun initiateMatch(playerA: Player, playerB: Player): Pair<Player, Player> {
        handle.transactionIsolationLevel = TransactionIsolationLevel.REPEATABLE_READ
        handle.createUpdate(
            """
                update matches set match_state = 'ONGOING' where id = :matchId
            """.trimIndent()
        )
        return playerA to playerB
    }

    /**
     * Gets the match by its id.
     * @param matchId id of the match
     * @return the match or null if not found
     */
    override fun getMatchById(matchId: Int): Match? =
        handle.createQuery("select * from matches where id = :matchId")
            .bind("matchId", matchId)
            .mapTo<Match>()
            .singleOrNull()

    /**
     * Gets the state of the match.
     * @param matchId id of the match
     * @return state of the match
     */
    override fun getMatchState(matchId: Int): MatchState =
        handle.createQuery("select match_state from matches where id = :matchId")
            .bind("matchId", matchId)
            .mapTo<String>()
            .single().toMatchState()

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
        handle.createQuery("select match_outcome from matches where id = :matchId and match_outcome = 'finished'")
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
            update matches set match_outcome = :outcome where id = :matchId
            """.trimIndent()
        )
            .bind("matchId", matchId)
            .bind("outcome", outcome.name)
            .execute()
    }

    /**
     * Gets the rule of the match.
     * @param matchId id of the match
     * @return the rule
     */
    override fun getMatchRule(matchId: Int): Rules =
        handle.createQuery(
            """
            select board_size, opening_rule, variant from rules where rules.id = (
            select id from matches where matches.id = :matchId
            )
            """.trimIndent()
        )
            .bind("matchId", matchId)
            .mapTo<Rules>()
            .single()

    /**
     * Gets the players of a match.
     * @param matchId id of the match
     * @return pair of players
     */
    override fun getMatchPlayers(matchId: Int): Pair<Player, Player>? =
        handle.createQuery(
            """
            select user_id, color from player where match_id = :matchId
            """.trimIndent()
        )
            .bind("matchId", matchId)
            .mapTo<Player>()
            .list()?.let {
                it[0] to it[1]
            }

    /**
     * Makes a move in the match.
     * @param matchId id of the match
     * @param move the position and color of the move
     */
    override fun makeMove(matchId: Int, move: Move) {
        handle.createUpdate(
            """
            insert into moves(match_id, player_id, ordinal, row, col)
            values (:match_id, :player_id, (select Count(match_id) from moves where moves.match_id = :match_id), :row, :col)
            """.trimIndent()
        )
            .bind("match_id", matchId)
            .bind("player_id", 1) // TODO: get player id
            .bind("color", move.color.name)
            .bind("row", move.position.x)
            .bind("col", move.position.y)
            .execute()
    }

    /**
     * Gets the moves of the match.
     * @param matchId id of the match
     * @return list of moves
     */
    override fun getAllMoves(matchId: Int): List<Move> =
        handle.createQuery(
            """
                select color, row, col from moves join player on
                moves.player_id = player.user_id and moves.match_id = :matchId
            """.trimIndent()
        )
            .bind("matchId", matchId)
            .mapTo<Move>()
            .toList()

    /**
     * Gets the last n moves of the match.
     * @param matchId id of the match
     * @param n number of moves to get
     * @return list of moves
     */
    override fun getLastNMoves(matchId: Int, n: Int): List<Move> =
        handle.createQuery(
            """
                select color, row, col from moves join player on
                moves.player_id = player.user_id and moves.match_id = player.match_id
                where player.match_id = :matchId
                order by moves.row desc limit :n
            """.trimIndent()
        )
            .bind("matchId", matchId)
            .bind("n", n)
            .mapTo<Move>()
            .toList()

    /**
     * Gets the turn of the match.
     * @param matchId id of the match
     * @return the id of the player for the current turn.
     */
    override fun getTurn(matchId: Int): Color =
        handle.createQuery("select Count(*) from moves where match_id = :matchId")
            .bind("matchId", matchId)
            .mapTo<Int>()
            .singleOrNull()?.toColor() ?: throw IllegalStateException("Match not found")
}
