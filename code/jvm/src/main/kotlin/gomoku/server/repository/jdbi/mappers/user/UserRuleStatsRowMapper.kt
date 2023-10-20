package gomoku.server.repository.jdbi.mappers.user

import gomoku.server.domain.user.RuleStats
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

/**
 * Maps a row of the result set to a [RuleStats]
 * @see RowMapper
 * @see RuleStats
 */
class UserRuleStatsRowMapper : RowMapper<RuleStats> {

    /**
     * Maps a row of the result set to a [RuleStats]
     * @param rs The result set to map
     * @param ctx The statement context
     * @return The [RuleStats] from the result set
     */
    override fun map(rs: ResultSet, ctx: StatementContext?): RuleStats {
        val ruleId = rs.getInt("rules_id")
        val gamesPlayed = rs.getInt("games_played")
        val elo = rs.getInt("elo")
        return RuleStats(ruleId, gamesPlayed, elo)
    }
}
