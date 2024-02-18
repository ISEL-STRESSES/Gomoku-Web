package gomoku.server.repository.jdbi.mappers.user

import gomoku.server.domain.user.RankingUserData
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

/**
 * Maps a row of the result set to a [RankingUserData]
 * @see RowMapper
 * @see RankingUserData
 */
class RankingUserDataRowMapper : RowMapper<RankingUserData> {
    /**
     * Maps a row of the result set to a [RankingUserData]
     * @param rs The result set to map
     * @param ctx The statement context
     * @return The [RankingUserData] from the result set
     */
    override fun map(rs: ResultSet, ctx: StatementContext?): RankingUserData {
        return RankingUserData(
            uuid = rs.getInt("user_id"),
            rank = rs.getInt("rank"),
            username = rs.getString("username"),
            ruleId = rs.getInt("rule_id"),
            gamesPlayed = rs.getInt("games_played"),
            elo = rs.getInt("elo")
        )
    }
}
