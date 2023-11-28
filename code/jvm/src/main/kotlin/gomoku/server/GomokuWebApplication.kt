package gomoku.server

import gomoku.server.domain.Author
import gomoku.server.domain.ServerInfo
import gomoku.server.domain.Socials
import gomoku.server.domain.user.Sha256TokenEncoder
import gomoku.server.domain.user.UsersDomainConfig
import gomoku.server.repository.configureWithAppRequirements
import kotlinx.datetime.Clock
import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import kotlin.time.Duration.Companion.hours

/**
 * Entry point of the server application.
 */
@SpringBootApplication
class GomokuWebApplication {

    /**
     * Creates a JDBI instance.
     */
    @Bean
    fun jdbi() = Jdbi.create(
        PGSimpleDataSource().apply {
            setURL(Environment.getDbUrl())
        }
    ).configureWithAppRequirements()

    /**
     * Creates a password encoder.
     */
    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    /**
     * Creates a token encoder.
     */
    @Bean
    fun tokenEncoder() = Sha256TokenEncoder()

    /**
     * Creates a clock.
     */
    @Bean
    fun clock() = Clock.System

    /**
     * Creates a user domain configuration.
     * @return The user domain configuration.
     */
    @Bean
    fun userDomainConfig() = UsersDomainConfig(
        tokenSizeInBytes = 256 / 8,
        tokenTtl = 24.hours,
        tokenRollingTtl = 1.hours,
        maxTokensPerUser = 3
    )

    /**
     * Creates server info.
     * @return The server info.
     */
    @Bean
    fun serverInfo() = ServerInfo(
        version = "0.1.1",
        authors = listOf(
            Author(
                studentID = 48335,
                name = "Rodrigo Correia",
                email = "A48335@alunos.isel.pt",
                listOf(
                    Socials(
                        name = "GitHub",
                        url = "https://github.com/RodrigoHCorreia"
                    )
                )
            ),
            Author(
                studentID = 48331,
                name = "André Matos",
                email = "A48331@alunos.isel.pt",
                listOf(
                    Socials(
                        name = "GitHub",
                        url = "https://github.com/Matos16"
                    )
                )
            ),
            Author(
                studentID = 48253,
                name = "Carlos Pereira",
                email = "A48253@alunos.isel.pt",
                listOf(
                    Socials(
                        name = "GitHub",
                        url = "https://github.com/Sideghost"
                    )
                )
            )
        )
    )
}

/**
 * Entry point of the server.
 */
fun main(args: Array<String>) {
    runApplication<GomokuWebApplication>(*args)
}
