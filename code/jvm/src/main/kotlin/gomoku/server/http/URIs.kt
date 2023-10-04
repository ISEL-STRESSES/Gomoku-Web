package gomoku.server.http

import org.springframework.web.util.UriTemplate
import java.net.URI

object URIs {

    const val HOME = "/"

    fun home() = URI(HOME)

    object Users {
        const val ROOT = "/"
        const val GET_BY_ID = "/{id}"
        const val TOKEN = "/token"
        const val CREATE = "/create"
        const val LOGOUT = "/logout"
        const val RANKING = "/ranking"

        fun getByID(id: Int) = UriTemplate(GET_BY_ID).expand(id)
        fun token() = URI(LOGIN)
        fun create() = URI(CREATE)
    }

    object Games {
        const val ROOT = "/games"
        const val HUB = "/"

        fun hub() = URI(ROOT + HUB)
    }
}
