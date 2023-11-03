package gomoku.server.repository.jdbi.mappers.game

import gomoku.server.domain.game.rules.Rules
import gomoku.server.domain.game.rules.buildRule
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

/**
 * Maps a row of the result set to a [Rules]
 * @see RowMapper
 * @see Rules
 */
class GameRuleRowMapper : RowMapper<Rules> {

    /**
     * Maps a row of the result set to a [Rules]
     * @param rs The result set to map
     * @param ctx The statement context
     * @return The [Rules] from the result set
     */
    override fun map(rs: ResultSet, ctx: StatementContext?): Rules {
        return buildRule(
            rs.getInt("id"),
            rs.getInt("board_size"),
            rs.getString("variant"),
            rs.getString("opening_rule")
        )
    }
}
