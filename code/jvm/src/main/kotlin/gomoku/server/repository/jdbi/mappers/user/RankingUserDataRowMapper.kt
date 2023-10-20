package gomoku.server.repository.jdbi.mappers.user

import gomoku.server.domain.user.RankingUserData
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

class RankingUserDataRowMapper : RowMapper<RankingUserData> {
    override fun map(rs: ResultSet, ctx: StatementContext?): RankingUserData {
        return RankingUserData(
            uuid = rs.getInt("user_id"),
            username = rs.getString("username"),
            ruleId = rs.getInt("rule_id"),
            gamesPlayed = rs.getInt("games_played"),
            elo = rs.getInt("elo")
        )
    }
}