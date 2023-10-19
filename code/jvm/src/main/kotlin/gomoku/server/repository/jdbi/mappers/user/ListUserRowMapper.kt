package gomoku.server.repository.jdbi.mappers.user

import gomoku.server.domain.user.ListUserData
import gomoku.server.domain.user.UserRuleStats
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

/**
 * Maps a row of the result set to a [ListUserData]
 * @see RowMapper
 * @see ListUserData
 */
class ListUserRowMapper : RowMapper<ListUserData> {

    /**
     * Maps a row of the result set to a [ListUserData]
     * @param rs The result set to map
     * @param ctx The statement context
     * @return The [ListUserData] from the result set
     */
    override fun map(rs: ResultSet, ctx: StatementContext?): ListUserData {
        val uuid = rs.getInt("user_id")
        val username = rs.getString("username")
        val ruleId = rs.getInt("rule_id")
        val gamesPlayed = rs.getInt("games_played")
        val elo = rs.getInt("elo")
        return ListUserData(uuid, username, listOf(UserRuleStats(ruleId, gamesPlayed, elo)))
    }
}
