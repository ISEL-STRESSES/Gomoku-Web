package gomoku.server.repository.jdbi.mappers.user

import gomoku.server.domain.user.UserData
import gomoku.server.domain.user.UserRuleStats
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

/**
 * Maps rows from the database to a [UserData] object
 * @see RowMapper
 * @see UserData
 */
class UserDataRowMapper : RowMapper<UserData> {

    /**
     * Maps a row of the result set to a [UserData]
     * @param rs The result set to map
     * @param ctx The statement context
     * @return The [UserData] from the result set
     */
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
