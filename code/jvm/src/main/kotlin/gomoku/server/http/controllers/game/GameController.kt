package gomoku.server.http.controllers.game

import gomoku.server.domain.user.AuthenticatedUser
import gomoku.server.http.URIs
import gomoku.server.services.game.GameService
import gomoku.server.services.game.MatchmakingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

// BIG TODO add response entity to all endpoints

@RestController(URIs.Game.ROOT)
class GameController(private val gameService: GameService) {

    @GetMapping(URIs.Game.HUB)
    fun games(
        @RequestParam offset: Int = 0,
        @RequestParam limit: Int = 10,
        @RequestParam authenticatedUser: AuthenticatedUser,
        @RequestParam gameState: String? = "ONGOING"
    ) = gameService.getMatches(offset, limit, authenticatedUser.user.uuid, gameState)

    @GetMapping(URIs.Game.GET_BY_ID)
    fun gameDetails(@RequestParam gameId: Int, @RequestParam authenticatedUser: AuthenticatedUser) =
        gameService.getGame(gameId)

    @GetMapping(URIs.Game.GAME_RULES)
    fun rules() = gameService.getAvailableRules()

    @PostMapping(URIs.Game.MAKE_PLAY)
    fun makePlay(
        @RequestParam gameId: Int,
        @RequestParam userId: Int,
        @RequestParam pos: Int
    ) = gameService.makeMove(gameId, userId, pos)

    @PostMapping(URIs.Game.MATCH_MAKE)
    fun startMatchmaking(@RequestParam ruleId: Int, @RequestParam userId: Int): MatchmakingResult {
        return gameService.startMatchmakingProcess(ruleId, userId)
    }

    @PostMapping(URIs.Game.LEAVE_LOBBY)
    fun leaveLobby(@RequestParam lobbyId: Int, @RequestParam userId: Int): Boolean {
        return gameService.leaveLobby(userId)
    }

    @GetMapping(URIs.Game.JOIN)
    fun getCurrentTurnPlayerId(@RequestParam matchId: Int): Int? {
        return gameService.getCurrentTurnPlayerId(matchId)
    }
}
