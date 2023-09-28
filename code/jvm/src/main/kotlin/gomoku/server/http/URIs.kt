package gomoku.server.http

import org.springframework.web.util.UriTemplate
import java.net.URI

object URIs {

    const val HOME = "/"
    const val LOGIN = "/login"
    const val REGISTER = "/register"
    const val LOGOUT = "/logout"

    fun home() = URI(HOME)

    object Users {
        const val ROOT = "/users"
        const val HUB = "/"
        const val BY_ID = "/{id}"
        const val RANKING = "/ranking"

        fun userByID(id: Int) = UriTemplate(BY_ID).expand(id)
        fun login() = URI(LOGIN)
        fun register() = URI(REGISTER)
    }

    object Games {
        const val ROOT = "/games"
        const val HUB = "/"

        fun hub() = URI(ROOT + HUB)
    }
}

