package gomoku.server.repository.jdbi.mappers

import gomoku.server.domain.game.player.PlayerRuleStats
import gomoku.server.domain.game.rules.buildRule
import gomoku.server.domain.user.UserData
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

/**
 * Maps rows from the database to a [UserData] object
 */
class UserDataRowMapper : RowMapper<UserData> {
    override fun map(rs: ResultSet, ctx: StatementContext): UserData {
        val rule = buildRule(
            boardMaxSize = rs.getInt("board_size"),
            variantName = rs.getString("variant"),
            openingRuleName = rs.getString("opening_rule")
        )

        val playerRuleStats = mutableListOf<PlayerRuleStats>()
        var previousUserId = rs.getInt("id")
        while (rs.next()) {
            if (previousUserId != rs.getInt("id")) {
                return UserData(
                    uuid = rs.getInt("id"),
                    username = rs.getString("username"),
                    playerRuleStats = playerRuleStats.toList()
                )
            } else {
                playerRuleStats.add(
                    PlayerRuleStats(
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
            playerRuleStats = playerRuleStats
        )
    }
}
