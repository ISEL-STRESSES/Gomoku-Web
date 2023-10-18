package gomoku.server.repository

import gomoku.server.repository.jdbi.mappers.FinishedMatchRowMapper
import gomoku.server.repository.jdbi.mappers.InstantMapper
import gomoku.server.repository.jdbi.mappers.LobbyRowMapper
import gomoku.server.repository.jdbi.mappers.MatchRowMapper
import gomoku.server.repository.jdbi.mappers.MatchRuleRowMapper
import gomoku.server.repository.jdbi.mappers.user.ListUserRowMapper
import gomoku.server.repository.jdbi.mappers.user.PasswordValidationInfoMapper
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
    registerRowMapper(ListUserRowMapper())
    registerRowMapper(UserDataRowMapper())
    registerRowMapper(UserRuleStatsRowMapper())
    registerRowMapper(MatchRuleRowMapper())
    registerRowMapper(LobbyRowMapper())
    registerRowMapper(MatchRowMapper())
    registerRowMapper(FinishedMatchRowMapper())

    return this
}
