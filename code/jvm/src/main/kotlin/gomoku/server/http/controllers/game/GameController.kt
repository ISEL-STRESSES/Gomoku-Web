package gomoku.server.http.controllers.game

import gomoku.server.services.game.GameService
import org.springframework.web.bind.annotation.RestController

@RestController()
class GameController(private val gameService: GameService) {

}