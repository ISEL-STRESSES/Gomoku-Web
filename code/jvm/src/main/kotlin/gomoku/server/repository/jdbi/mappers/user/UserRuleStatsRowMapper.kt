package gomoku.server.repository.jdbi.mappers.user

import gomoku.server.domain.user.UserRuleStats
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

/**
 * Maps a row of the result set to a [UserRuleStats]
 * @see RowMapper
 * @see UserRuleStats
 */
class UserRuleStatsRowMapper : RowMapper<UserRuleStats> {

    /**
     * Maps a row of the result set to a [UserRuleStats]
     * @param rs The result set to map
     * @param ctx The statement context
     * @return The [UserRuleStats] from the result set
     */
    override fun map(rs: ResultSet, ctx: StatementContext?): UserRuleStats {
        val ruleId = rs.getInt("rules_id")
        val gamesPlayed = rs.getInt("games_played")
        val elo = rs.getInt("elo")
        return UserRuleStats(ruleId, gamesPlayed, elo)
    }
}
