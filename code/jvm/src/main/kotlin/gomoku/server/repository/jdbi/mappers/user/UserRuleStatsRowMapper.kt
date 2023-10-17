package gomoku.server.repository.jdbi.mappers.user

import gomoku.server.domain.user.UserRuleStats
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

class UserRuleStatsRowMapper : RowMapper<UserRuleStats> {
    override fun map(rs: ResultSet, ctx: StatementContext?): UserRuleStats {
        val ruleId = rs.getInt("rules_id")
        val gamesPlayed = rs.getInt("games_played")
        val elo = rs.getInt("elo")
        return UserRuleStats(ruleId, gamesPlayed, elo)
    }
}