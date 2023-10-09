package gomoku.server.repository.jdbi.mappers

import gomoku.server.domain.game.rules.Rule
import gomoku.server.domain.game.rules.RuleStats
import gomoku.server.domain.user.UserData
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

/**
 * Maps rows from the database to a [UserData] object
 */
class UserDataRowMapper : RowMapper<UserData> {
    override fun map(rs: ResultSet, ctx: StatementContext): UserData {
        val rule = Rule.buildRule(
            boardMaxSize = rs.getInt("board_size"),
            variantName = rs.getString("variant"),
            openingRuleName = rs.getString("opening_rule")
        )

        val ruleStats = mutableListOf<RuleStats>()
        var previousUserId = rs.getInt("id")
        while (rs.next()) {
            if (previousUserId != rs.getInt("id")) {
                return UserData(
                    uuid = rs.getInt("id"),
                    username = rs.getString("username"),
                    ruleStats = ruleStats.toList()
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
