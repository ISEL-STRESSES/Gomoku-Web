import gomoku.server.http.URIs
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping(URIs.Games.ROOT)
class GameController {

    @RequestMapping(URIs.Games.HUB)
    fun getHub(

    ) {

    }
}
