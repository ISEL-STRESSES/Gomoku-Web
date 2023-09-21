package gomoku.server.http.controllers

import gomoku.server.domain.Author
import gomoku.server.domain.ServerInfo
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HomeController {

    @GetMapping(URIs.HOME)
    fun getHome(request: HttpServletRequest) : ResponseEntity<ServerInfo> {
        return ResponseEntity.ok(serverInfo)
    }

    companion object {
        private val authors = listOf(
            Author(
                name = "Rodrigo Correia",
                email = "A48335@alunos.isel.pt",
                gitHub = "github.com/rodrigohcorreia",
                id = 48335
                ),
            Author(
                name = "Adolfo Morgado",
                email = "A48281@alunos.isel.pt",
                gitHub = "github.com/admorgado",
                id = 48281
            ),
            Author(
                name = "Carlos Pereira",
                email = "A48253@alunos.isel.pt",
                gitHub = "github.com/Sideghost",
                id = 48253
            )
        )

        private val serverInfo = ServerInfo(
            version = "0.0.1",
            authors = authors
        )
    }
}