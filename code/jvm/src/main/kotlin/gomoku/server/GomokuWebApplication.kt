package gomoku.server

import gomoku.server.domain.Author
import gomoku.server.domain.ServerInfo
import gomoku.server.domain.user.Sha256TokenEncoder
import gomoku.server.domain.user.UserDomainConfig
import gomoku.server.http.pipeline.AuthenticatedUserArgumentResolver
import gomoku.server.http.pipeline.AuthenticationInterceptor
import kotlinx.datetime.Clock
import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import kotlin.time.Duration.Companion.hours

@SpringBootApplication
class GomokuWebApplication {
    @Bean
    fun jdbi() = Jdbi.create(
        PGSimpleDataSource().apply {
            setURL(Environment.getDbUrl())
        }
    )

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun tokenEncoder() = Sha256TokenEncoder()

    @Bean
    fun clock() = Clock.System

    @Bean
    fun userDomainConfig() = UserDomainConfig(
        tokenSizeInBytes = 256 / 8,
        tokenTtl = 24.hours,
        tokenRollingTtl = 1.hours,
        maxTokensPerUser = 3
    )

    @Bean
    fun serverInfo() = ServerInfo(
        version = "0.0.1",
        authors = listOf(
            Author(
                studentID = 48335,
                name = "Rodrigo Correia",
                email = "A48335@alunos.isel.pt"
            ),
            Author(
                studentID = 48331,
                name = "André Matos",
                email = "A48331@alunos.isel.pt"
            ),
            Author(
                studentID = 48253,
                name = "Carlos Pereira",
                email = "A48253@alunos.isel.pt"
            )
        )
    )
}

@Configuration
class PipelineConfigurer(
    val authenticationInterceptor: AuthenticationInterceptor,
    val authenticatedUserArgumentResolver: AuthenticatedUserArgumentResolver
) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(authenticationInterceptor)
    }

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(authenticatedUserArgumentResolver)
    }
}

fun main(args: Array<String>) {
    runApplication<GomokuWebApplication>(*args)
}
