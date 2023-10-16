package gomoku.server.repository.jdbi.mappers

import gomoku.server.domain.game.Lobby
import gomoku.server.domain.game.rules.buildRule
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

class LobbyRowMapper : RowMapper<Lobby> {
    override fun map(rs: ResultSet, ctx: StatementContext?): Lobby {
        return Lobby(
            rule = buildRule(
                ruleId = rs.getInt("rules_id"),
                boardMaxSize = rs.getInt("board_size"),
                variantName = rs.getString("variant"),
                openingRuleName = rs.getString("opening_rule")
            ),
            userId = rs.getInt("user_id")
        )
    }
}
