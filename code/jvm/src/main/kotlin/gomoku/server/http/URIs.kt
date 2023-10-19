package gomoku.server.http

import org.springframework.web.util.UriTemplate
import java.net.URI

/**
 * Contains the URIs for the application
 */
object URIs {

    const val PREFIX = "/api"
    const val HOME = "$PREFIX/"

    fun home() = URI(HOME)

    /**
     * Contains the URIs for the users endpoints
     */
    object Users {
        const val ROOT = "/users"
        const val GET_BY_ID = "$ROOT/{id}"
        const val HOME = "$ROOT/me"
        const val TOKEN = "ROOT + /token"
        const val CREATE = "/create"
        const val LOGOUT = "/logout"
        const val RANKING = "/ranking/{ruleId}"
        const val USER_RANKING = "/ranking/{userId}/{ruleId}"
        const val USER_STATS = "/stats/{userId}"
        const val RANKING_SEARCH = "/ranking/{ruleId}/search"

        fun byID(id: Int) = UriTemplate(GET_BY_ID).expand(id)
        fun home() = URI(HOME)
        fun login() = URI(TOKEN)
        fun register() = URI(CREATE)
        fun logout() = URI(LOGOUT)
        fun ranking(ruleId: Int) = UriTemplate(RANKING).expand(ruleId)
        fun userRanking(userId: Int, ruleId: Int) = UriTemplate(USER_RANKING).expand(userId, ruleId)
        fun userStats(userId: Int) = UriTemplate(USER_STATS).expand(userId)
        fun rankingSearch(ruleId: Int) = UriTemplate(RANKING_SEARCH).expand(ruleId)
    }

    /**
     * Contains the URIs for the game endpoints
     */
    object Game {
        const val ROOT = "/game"
        const val HUB = "$ROOT/"
        const val GET_BY_ID = "$ROOT/{id}" // details
        const val MAKE_PLAY = "$ROOT/{id}/" // try to make a move
        const val GAME_RULES = "/rules"
        const val MATCH_MAKE = "$ROOT/{rulesId}"
        const val JOIN = "/{id}/join"
        const val LEAVE_GAME = "$ROOT/{id}/leave"
        const val LEAVE_LOBBY = "$ROOT{lobbyId}/leave"

        fun hub() = URI(HUB)
        fun byId(id: Int) = UriTemplate(GET_BY_ID).expand(id)
        fun joinLobby(rulesId: Int) = UriTemplate(MATCH_MAKE).expand(rulesId)
        fun leaveLobby(lobbyId: Int) = UriTemplate(LEAVE_LOBBY).expand(lobbyId)
        fun leaveGame(gameID: Int) = UriTemplate(LEAVE_GAME).expand(gameID)
        fun play(gameId: Int) = UriTemplate(MAKE_PLAY).expand(gameId)
    }
}
