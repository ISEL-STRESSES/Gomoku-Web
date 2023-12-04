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
        const val ROOT = "$PREFIX/users"
        const val GET_BY_ID = "/{id}"
        const val HOME = "/me"
        const val TOKEN = "/token"
        const val CREATE = "/create"
        const val LOGOUT = "/logout"
        const val RANKING = "/ranking/{ruleId}"
        const val USER_RANKING = "/{userId}/ranking/{ruleId}"
        const val USER_STATS = "/stats/{userId}"

        fun byID(id: Int) = UriTemplate(ROOT + GET_BY_ID).expand(id)
        fun home() = URI(HOME)
        fun login() = URI(TOKEN)
        fun register() = URI(CREATE)
        fun logout() = URI(LOGOUT)
        fun ranking(ruleId: Int) = UriTemplate(ROOT + RANKING).expand(ruleId)
        fun userRanking(userId: Int, ruleId: Int) = UriTemplate(ROOT + USER_RANKING).expand(userId, ruleId)
        fun userStats(userId: Int) = UriTemplate(ROOT + USER_STATS).expand(userId)
    }

    /**
     * Contains the URIs for the game endpoints
     */
    object Game {
        const val ROOT = "$PREFIX/game"
        const val HUB = "/"
        const val GET_BY_ID = "/{gameId}"
        const val MAKE_PLAY = "/{gameId}/play"
        const val GAME_RULES = "/rules"
        const val GAME_RULE_ID = "/rules/{ruleId}"
        const val TURN = "/{gameId}/turn"
        const val FORFEIT_GAME = "/{gameId}/forfeit"

        fun hub() = URI(HUB)
        fun byId(id: Int) = UriTemplate(ROOT + GET_BY_ID).expand(id)
        fun turn(gameID: Int) = UriTemplate(ROOT + TURN).expand(gameID)
        fun forfeitGame(gameID: Int) = UriTemplate(ROOT + FORFEIT_GAME).expand(gameID)
        fun play(gameId: Int) = UriTemplate(ROOT + MAKE_PLAY).expand(gameId)
    }

    object Lobby {
        const val ROOT = "$PREFIX/lobby"
        const val GET_LOBBIES = "/"
        const val MATCH_MAKE = "/start"
        const val CREATE_LOBBY = "/create"
        const val JOIN_LOBBY = "/join"
        const val LEAVE_LOBBY = "/leave"
        const val GET_LOBBY_BY_ID = "/{lobbyId}"

        fun createLobby(ruleId: Int) = UriTemplate(ROOT + CREATE_LOBBY).expand(ruleId)
        fun leaveLobby(lobbyId: Int) = UriTemplate(ROOT + LEAVE_LOBBY).expand(lobbyId)

        fun joinLobby(lobbyId: Int) = UriTemplate(ROOT + JOIN_LOBBY).expand(lobbyId)
        fun matchMake() = URI(ROOT + MATCH_MAKE)

        fun getLobbyById(lobbyId: Int) = UriTemplate(ROOT + GET_LOBBY_BY_ID).expand(lobbyId)

        fun getLobbies() = URI(ROOT + GET_LOBBIES)
    }
}
