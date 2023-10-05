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
        const val RANKING = "/ranking"

        fun byID(id: Int) = UriTemplate(GET_BY_ID).expand(id)
        fun home() = URI(HOME)
        fun login() = URI(TOKEN)
        fun register() = URI(CREATE)
    }

    object Games {
        const val ROOT = "/games"
        const val HUB = "/"

        fun hub() = URI(ROOT + HUB)
    }
}
