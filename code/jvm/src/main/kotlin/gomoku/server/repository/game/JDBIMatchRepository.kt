package gomoku.server.repository.game

import gomoku.server.domain.game.match.MatchOutcome
import gomoku.server.domain.game.match.MatchState
import gomoku.server.domain.game.match.toMatchState
import gomoku.server.domain.game.player.Color
import gomoku.server.domain.game.player.Move
import gomoku.server.domain.game.player.toColor
import gomoku.server.domain.game.rules.Rule
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
    override fun getMatchOutcome(matchId: Int): MatchOutcome? =
        handle.createQuery("select match_outcome from matches where id = :matchId and match_outcome = 'finished'")
            .bind("matchId", matchId)
            .mapTo<MatchOutcome>()
            .singleOrNull()

    override fun getAllMoves(matchId: Int): List<Move> =
        handle.createQuery(
            """
                select color, row, col from moves join player on
                moves.player_user_id = player.user_id and player_match_id = :matchId
            """.trimIndent()
        )
            .bind("matchId", matchId)
            .mapTo<Move>()
            .toList()

    override fun getLastNMoves(matchId: Int, n: Int): List<Move> {
        TODO("Not yet implemented")
    }

    override fun makeMove(matchId: Int, move: Move) {
        handle.createUpdate(
            """
            insert into moves(player_match_id, player_user_id, row, col)
            values (:match_id, :player_id, :row, :col)
            """.trimIndent()
        )
            .bind("match_id", matchId)
            .bind("player_id", 1) // TODO: get player id
            .bind("color", move.color.name)
            .bind("row", move.position.x)
            .bind("col", move.position.y)
            .execute()
    }

    override fun getMatchState(matchId: Int): MatchState =
        handle.createQuery("select match_state from matches where id = :matchId")
            .bind("matchId", matchId)
            .mapTo<String>()
            .single().toMatchState()

    override fun setMatchState(matchId: Int, state: MatchState) {
        TODO("Not yet implemented")
    }

    override fun getTurn(matchId: Int): Color =
        handle.createQuery("select Count(*) from moves where player_match_id = :matchId")
            .bind("matchId", matchId)
            .mapTo<Int>()
            .singleOrNull()?.toColor() ?: throw IllegalStateException("Match not found")
}
