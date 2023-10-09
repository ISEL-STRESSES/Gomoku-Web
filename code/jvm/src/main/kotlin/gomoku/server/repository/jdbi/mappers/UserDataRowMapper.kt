package gomoku.server.repository.jdbi.mappers;

import gomoku.server.domain.OpeningRule
import gomoku.server.domain.Rule
import gomoku.server.domain.RuleStats
import gomoku.server.domain.RuleVariant
import gomoku.server.domain.user.UserData
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

public class UserDataRowMapper : RowMapper<UserData> {
    override fun map(rs: ResultSet, ctx: StatementContext): UserData {
        val rule = Rule(
            boardSize = rs.getInt("board_size"),
            variant = RuleVariant.fromString(rs.getString("variant")),
            openingRule = OpeningRule.fromString(rs.getString("opening_rule"))
        )
        val ruleStats = mutableListOf<RuleStats>()
        var previousUserId = rs.getInt("id")
        while (rs.next()){
            if (previousUserId != rs.getInt("id")) {
                return UserData(
                    uuid = rs.getInt("id"),
                    username = rs.getString("username"),
                    ruleStats = ruleStats
                )
            } else {
                ruleStats.add(
                    RuleStats(
                        rule = rule,
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
            ruleStats = ruleStats
        )
    }
}
