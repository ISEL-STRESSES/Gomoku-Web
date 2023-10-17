package gomoku.server.repository.jdbi.mappers.user

import gomoku.server.domain.user.UserData
import gomoku.server.domain.user.UserRuleStats
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

/**
 * Maps rows from the database to a [UserData] object
 */
class UserDataRowMapper : RowMapper<UserData> {
    override fun map(rs: ResultSet, ctx: StatementContext): UserData {
        val uuid = rs.getInt("user_id")
        val username = rs.getString("username")

        val userRuleStats = mutableListOf<UserRuleStats>()

        do {
            val ruleId = rs.getInt("rule_id")
            val gamesPlayed = rs.getInt("games_played")
            val elo = rs.getInt("elo")

            userRuleStats.add(UserRuleStats(ruleId, gamesPlayed, elo))
        } while (rs.next())

        return UserData(uuid, username, userRuleStats)
    }
}
