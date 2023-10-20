package gomoku.server.repository.jdbi.mappers

import gomoku.server.domain.game.rules.OpeningRule
import gomoku.server.domain.game.rules.RuleVariant
import gomoku.server.domain.game.rules.RulesRepresentation
import gomoku.server.domain.game.rules.toBoardSize
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

/**
 * Maps a row of the result set to a [RulesRepresentation]
 * @see RowMapper
 * @see RulesRepresentation
 */
class RulesRepresentationRowMapper : RowMapper<RulesRepresentation> {
    /**
     * Maps a row of the result set to a [RulesRepresentation]
     * @param rs The result set to map
     * @param ctx The statement context
     * @return The [RulesRepresentation] from the result set
     */
    override fun map(rs: ResultSet, ctx: StatementContext?): RulesRepresentation {
        return RulesRepresentation(
            ruleId = rs.getInt("id"),
            boardSize = (rs.getInt("board_size")).toBoardSize(), // Assuming boardSize is an enum
            variant = RuleVariant.valueOf(rs.getString("variant")), // Assuming variant is an enum
            openingRule = OpeningRule.valueOf(rs.getString("opening_rule")) // Assuming openingRule is an enum
        )
    }
}
