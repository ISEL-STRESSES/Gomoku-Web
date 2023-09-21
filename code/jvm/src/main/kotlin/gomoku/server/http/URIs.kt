import java.net.URI

object URIs {

    const val HOME="/"
    const val LOGIN="/login"
    const val REGISTER="/register"

    fun home() = URI(HOME)
    fun login() = URI(LOGIN)
    fun register() = URI(REGISTER)

    object Users {
        const val ROOT = "/users"
        const val BY_ID = "/{id}"

    }

    object Games {
        const val ROOT = "/games"
        const val HUB = "/"

        fun hub() = URI(ROOT + HUB)
    }
}

