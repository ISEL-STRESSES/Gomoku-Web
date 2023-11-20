package gomoku.server.repository

import com.fasterxml.jackson.databind.ObjectMapper
import gomoku.server.domain.game.game.move.MoveContainer
import gomoku.server.domain.game.game.move.MoveContainerMixin
import gomoku.server.repository.jdbi.mappers.InstantMapper
import gomoku.server.repository.jdbi.mappers.game.FinishedGameRowMapper
import gomoku.server.repository.jdbi.mappers.game.GameRowMapper
import gomoku.server.repository.jdbi.mappers.game.GameRuleRowMapper
import gomoku.server.repository.jdbi.mappers.game.LobbyRowMapper
import gomoku.server.repository.jdbi.mappers.game.OngoingGameRowMapper
import gomoku.server.repository.jdbi.mappers.game.RulesRowMapper
import gomoku.server.repository.jdbi.mappers.user.PasswordValidationInfoMapper
import gomoku.server.repository.jdbi.mappers.user.RankingUserDataRowMapper
import gomoku.server.repository.jdbi.mappers.user.TokenValidationInfoMapper
import gomoku.server.repository.jdbi.mappers.user.UserDataRowMapper
import gomoku.server.repository.jdbi.mappers.user.UserRowMapper
import gomoku.server.repository.jdbi.mappers.user.UserRuleStatsRowMapper
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.postgres.PostgresPlugin

/**
 * Configures the JDBI instance with the requirements of the application
 * @return the configured JDBI instance
 */
fun Jdbi.configureWithAppRequirements(): Jdbi {
    installPlugin(KotlinPlugin())
    installPlugin(PostgresPlugin())

    registerColumnMapper(PasswordValidationInfoMapper())
    registerColumnMapper(TokenValidationInfoMapper())
    registerColumnMapper(InstantMapper())
    registerRowMapper(UserRowMapper())
    registerRowMapper(UserDataRowMapper())
    registerRowMapper(UserRuleStatsRowMapper())
    registerRowMapper(GameRuleRowMapper())
    registerRowMapper(LobbyRowMapper())
    registerRowMapper(GameRowMapper())
    registerRowMapper(FinishedGameRowMapper())
    registerRowMapper(OngoingGameRowMapper())
    registerRowMapper(RulesRowMapper())
    registerRowMapper(RankingUserDataRowMapper())
    registerRowMapper(OngoingGameRowMapper())

    ObjectMapper().apply {
        addMixIn(MoveContainer::class.java, MoveContainerMixin::class.java)
    }
    return this
}
