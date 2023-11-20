package gomoku.server.http.controllers

import gomoku.server.domain.ServerInfo
import gomoku.server.http.URIs
import gomoku.server.http.responses.SirenHomeResponses
import gomoku.server.http.responses.response
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Controller for home-related endpoints
 */
@RestController
class HomeController(private val serverInfo: ServerInfo) {

    /**
     * Gets the home page
     * @param request The request
     * @return The home page
     */
    @GetMapping(URIs.HOME)
    fun getHome(request: HttpServletRequest): ResponseEntity<*> {
        return SirenHomeResponses.siren(serverInfo).response(200)
    }
}
