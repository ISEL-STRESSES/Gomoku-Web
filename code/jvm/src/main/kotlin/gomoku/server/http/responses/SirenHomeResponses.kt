package gomoku.server.http.responses

import gomoku.server.domain.ServerInfo
import gomoku.server.http.Rel
import gomoku.server.http.URIs
import gomoku.server.http.infra.SirenMediaType
import org.springframework.http.HttpMethod
import java.net.URI
import gomoku.server.http.infra.siren

/**
 * TODO
 */
object SirenHomeResponses {

    /**
     * TODO
     */
    fun siren(body: ServerInfo) =
        siren {
            clazz(Rel.HOME.value)
            property(body)
            action(Rel.SIGNUP.value, "Sign up", HttpMethod.POST, URI(URIs.Users.ROOT + URIs.Users.CREATE), SirenMediaType, listOf(ActionHomeFieldModel("username", "text"), ActionHomeFieldModel("password", "password")))
            action(Rel.LOGIN.value, "Login", HttpMethod.POST, URI(URIs.Users.ROOT + URIs.Users.TOKEN), SirenMediaType, listOf(ActionHomeFieldModel("username", "text"), ActionHomeFieldModel("password", "password")))
            action(Rel.LOGOUT.value, "Logout", HttpMethod.POST, URI(URIs.Users.ROOT + URIs.Users.LOGOUT), SirenMediaType, emptyList())
            action(Rel.SEARCH_RANKING.value, "Search ranking", HttpMethod.GET, URI(URIs.Users.ROOT + "/ranking/1?limit=10"), SirenMediaType, listOf(ActionHomeFieldModel("ruleId", "number", "1"), ActionHomeFieldModel("search", "text"), ActionHomeFieldModel("limit", "number","10"), ActionHomeFieldModel("offset", "number")))
            link(URIs.HOME, Rel.SELF)
        }
}

data class ActionHomeFieldModel(
    val name: String,
    val type: String? = null,
    val value: String? = null
)