package gomoku.server.repository.game

import gomoku.server.domain.Rule
import gomoku.server.domain.game.MatchOutcome
import gomoku.server.domain.game.MatchState
import gomoku.server.domain.game.board.Color
import gomoku.server.domain.game.board.Move
import gomoku.server.domain.game.board.toColor
import gomoku.server.domain.game.toMatchState
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.transaction.TransactionIsolationLevel

class JDBIMatchRepository(private val handle: Handle) : MatchRepository {

    /**
     * Creates a new set of rules.
     * @param rule rules of the game
     * @return id of the rule
     */
    private fun createRule(rule: Rule): Int {
        return handle.createUpdate(
            """
            insert into rules(board_size, opening_rule, variant)
            values (:boardSize, :openingRule, :variant)
            """.trimIndent()
        )
            .bind("boardSize", rule.boardSize)
            .bind("openingRule", rule.openingRule)
            .bind("variant", rule.variant)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .one()
    }

    /**
     * Gets the id of a set of rules, if not found it creates a new one.
     * @param rule rules of the game
     * @return id of the rule
     */
    override fun getRuleId(rule: Rule): Int {
        val existingRule = handle.createQuery(
            """
                select id from rules where 
                board_size = :boardSize and 
                opening_rule = :openingRule and 
                variant = :variant
            """.trimIndent()
        )
            .bind("boardSize", rule.boardSize)
            .bind("openingRule", rule.openingRule)
            .bind("variant", rule.variant)
            .mapTo(Int::class.java)
            .singleOrNull()
        return existingRule ?: createRule(rule)
    }

    override fun initiateMatch(playerAId: Int, playerBId: Int): Int {
        handle.transactionIsolationLevel = TransactionIsolationLevel.REPEATABLE_READ
        TODO("Not yet implemented")
    }

    /**
     * Gets the winner of the match.
     * @param matchId id of the match
     * @return id of the winner, draw or null if the match is not finished
     */
    override fun getWinner(matchId: Int): MatchOutcome? =
        handle.createQuery("select match_outcome from matches where id = :matchId and match_outcome = 'finished'")
            .bind("matchId", matchId)
            .mapTo<MatchOutcome>()
            .singleOrNull()

    override fun getMoves(matchId: Int, rule: Rule): List<Move> =
        handle.createQuery(
            """
                select color, row, col from moves join player 
                on player.user_id = player_id and moves.match_id = player.match_id and moves.rules_id = player.rules_id
                where moves.rules_id = :rule_id and moves.match_id = :matchId
            """.trimIndent()
        )
            .bind("matchId", matchId)
            .bind("rule_id", getRuleId(rule))
            .mapTo<Move>()
            .toList()

    override fun makeMove(matchId: Int, rule: Rule, move: Move) {
        if (getMoves(matchId, rule).any { it.position == move.position }) throw IllegalStateException("Move already made")
        if (getTurn(matchId) != move.color) throw IllegalStateException("Not your turn")
        if (getMatchState(matchId) != MatchState.ONGOING) throw IllegalStateException("Match is not in progress")
        handle.createUpdate(
            """
            insert into moves(rules_id, match_id, player_id, row, col)
            values (:rule_id, :match_id, :player_id, :row, :col)
            """.trimIndent()
        )
            .bind("rule_id", getRuleId(rule))
            .bind("match_id", matchId)
            .bind("player_id", 1) // TODO: get player id
            .bind("color", move.color.toString())
            .bind("row", move.position.x)
            .bind("col", move.position.y)
            .execute()
    }

    override fun getMatchState(matchId: Int): MatchState =
        handle.createQuery("select match_state from matches where id = :matchId")
            .bind("matchId", matchId)
            .mapTo<String>()
            .single().toMatchState()

    override fun getTurn(matchId: Int): Color =
        handle.createQuery("select turn from matches where id = :matchId")
            .bind("matchId", matchId)
            .mapTo<String>()
            .singleOrNull()?.toColor() ?: throw IllegalStateException("Match not found")
}
