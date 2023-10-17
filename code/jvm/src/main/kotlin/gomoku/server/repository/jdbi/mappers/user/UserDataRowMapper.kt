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
        val userRuleStats = mutableListOf<UserRuleStats>()
        var previousUserId = rs.getInt("id")
        while (rs.next()) {
            if (previousUserId != rs.getInt("id")) {
                return UserData(
                    uuid = rs.getInt("id"),
                    username = rs.getString("username"),
                    userRuleStats = userRuleStats.toList()
                )
            } else {
                userRuleStats.add(
                    UserRuleStats(
                        ruleId = rs.getInt("rule_id"),
                        gamesPlayed = rs.getInt("games_played"),
                        elo = rs.getInt("elo")
                    )
                )
            }
            previousUserId = rs.getInt("id")
        }
        return UserData(
            uuid = rs.getInt("id"),
            username = rs.getString("username"),
            userRuleStats = userRuleStats
        )
    }
}
