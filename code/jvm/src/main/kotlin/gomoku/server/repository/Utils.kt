package gomoku.server.repository

import gomoku.server.repository.jdbi.mappers.*
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.postgres.PostgresPlugin

fun Jdbi.configureWithAppRequirements(): Jdbi {
    installPlugin(KotlinPlugin())
    installPlugin(PostgresPlugin())

    registerColumnMapper(PasswordValidationInfoMapper())
    registerColumnMapper(TokenValidationInfoMapper())
    registerColumnMapper(InstantMapper())
    registerRowMapper(UserRowMapper())
    registerRowMapper(UserDataRowMapper())
    registerRowMapper(MatchRuleRowMapper())
    registerRowMapper(LobbyRowMapper())

    return this
}
