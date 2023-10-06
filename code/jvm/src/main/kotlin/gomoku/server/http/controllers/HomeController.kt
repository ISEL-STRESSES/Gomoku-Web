package gomoku.server.http.controllers

import gomoku.server.domain.ServerInfo
import gomoku.server.http.URIs
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HomeController(private val serverInfo: ServerInfo) {

    @GetMapping(URIs.HOME)
    fun getHome(request: HttpServletRequest): ResponseEntity<ServerInfo> {
        return ResponseEntity.ok(serverInfo)
    }
}
