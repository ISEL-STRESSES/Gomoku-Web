package gomoku.server.http

import org.springframework.web.util.UriTemplate
import java.net.URI

object URIs {

    const val PREFIX = "/api"
    const val HOME = "$PREFIX/"

    fun home() = URI(HOME)

    object Users {
        const val ROOT = "/users"
        const val GET_BY_ID = "/{id}"
        const val HOME = "/me"
        const val TOKEN = "/token"
        const val CREATE = "/create"
        const val LOGOUT = "/logout"
        const val RANKING = "/ranking/{ruleId}"
        const val USER_RANKING = "/ranking/{userId}/{ruleId}"
        const val USER_STATS = "/stats/{userId}"

        fun byID(id: Int) = UriTemplate(GET_BY_ID).expand(id)
        fun home() = URI(HOME)
        fun login() = URI(TOKEN)
        fun register() = URI(CREATE)
        fun logout() = URI(LOGOUT)
        fun ranking(ruleId: Int) = UriTemplate(RANKING).expand(ruleId)
        fun userRanking(userId: Int, ruleId: Int) = UriTemplate(USER_RANKING).expand(userId, ruleId)
        fun userStats(userId: Int) = UriTemplate(USER_STATS).expand(userId)
    }

    object Games {
        const val ROOT = "/games"
        const val HUB = "/"

        fun hub() = URI(ROOT + HUB)
    }
}
