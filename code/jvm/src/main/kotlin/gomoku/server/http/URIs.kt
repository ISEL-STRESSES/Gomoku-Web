package gomoku.server.http

import org.springframework.web.util.UriTemplate
import java.net.URI

object URIs {

    const val HOME = "/"

    fun home() = URI(HOME)

    object Users {
        const val ROOT = "/users"
        const val BY_ID = "$ROOT/{id}"
        const val LOGIN = "$ROOT/login"
        const val REGISTER = "$ROOT/register"
        const val LOGOUT = "$ROOT/logout"

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
