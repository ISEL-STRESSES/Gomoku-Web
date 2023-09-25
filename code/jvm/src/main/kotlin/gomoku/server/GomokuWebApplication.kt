package gomoku.server

import gomoku.server.domain.Author
import gomoku.server.domain.ServerInfo
import gomoku.server.repository.memory.MemoryDataSource
import gomoku.server.repository.memory.MemoryTransactionManager
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@SpringBootApplication
class GomokuWebApplication{
    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun transactionManager() = MemoryTransactionManager(MemoryDataSource())

    @Bean
    fun serverInfo() = ServerInfo(
        version = "0.0.1",
        authors = listOf(
            Author(
                studentID = 48335,
                name = "Rodrigo Correia",
                email = "A48335@alunos.isel.pt",

                ),
            Author(
                studentID = 48281,
                name = "Adolfo Morgado",
                email = "A48281@alunos.isel.pt",

                ),
            Author(
                studentID = 48253,
                name = "Carlos Pereira",
                email = "A48253@alunos.isel.pt",

                )
        )
    )
}

fun main(args: Array<String>) {

    runApplication<GomokuWebApplication>(*args)

}
