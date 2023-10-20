package gomoku.server.repository.jdbi.mappers.user

import gomoku.server.domain.user.RuleStats
import gomoku.server.domain.user.UserStats
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

/**
 * Maps rows from the database to a [UserStats] object
 * @see RowMapper
 * @see UserStats
 */
class UserDataRowMapper : RowMapper<UserStats> {

    /**
     * Maps a row of the result set to a [UserStats]
     * @param rs The result set to map
     * @param ctx The statement context
     * @return The [UserStats] from the result set
     */
    override fun map(rs: ResultSet, ctx: StatementContext): UserStats {
        val uuid = rs.getInt("user_id")
        val username = rs.getString("username")

        val userRuleStats = mutableListOf<RuleStats>()

        do {
            val ruleId = rs.getInt("rule_id")
            val gamesPlayed = rs.getInt("games_played")
            val elo = rs.getInt("elo")

            userRuleStats.add(RuleStats(ruleId, gamesPlayed, elo))
        } while (rs.next())

        return UserStats(uuid, username, userRuleStats)
    }
}
