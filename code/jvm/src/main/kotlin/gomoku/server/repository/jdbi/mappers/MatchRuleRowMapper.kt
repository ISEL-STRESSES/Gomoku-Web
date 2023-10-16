package gomoku.server.repository.jdbi.mappers

import gomoku.server.domain.game.rules.Rules
import gomoku.server.domain.game.rules.buildRule
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

class MatchRuleRowMapper : RowMapper<Rules> {
    override fun map(rs: ResultSet, ctx: StatementContext?): Rules {
        return buildRule(
            rs.getInt("id"),
            rs.getInt("board_size"),
            rs.getString("variant"),
            rs.getString("opening_rule")
        )
    }
}
