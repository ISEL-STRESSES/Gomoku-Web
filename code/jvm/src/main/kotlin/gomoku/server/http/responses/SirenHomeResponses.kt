package gomoku.server.http.responses

import gomoku.server.domain.ServerInfo
import gomoku.server.http.Rel
import gomoku.server.http.URIs
import gomoku.server.http.infra.ActionFieldModel
import gomoku.server.http.infra.SirenMediaType
import gomoku.server.http.infra.siren
import org.springframework.http.HttpMethod
import java.net.URI

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
            action(
                Rel.SIGNUP.value,
                "Sign up",
                HttpMethod.POST,
                URI(URIs.Users.ROOT + URIs.Users.CREATE),
                SirenMediaType,
                listOf(
                    ActionFieldModel("username", "text"),
                    ActionFieldModel("password", "password")
                )
            )
            action(
                Rel.LOGIN.value,
                "Login",
                HttpMethod.POST,
                URI(URIs.Users.ROOT + URIs.Users.TOKEN),
                SirenMediaType,
                listOf(
                    ActionFieldModel("username", "text"),
                    ActionFieldModel("password", "password")
                )
            )
            action(
                Rel.LOGOUT.value,
                "Logout",
                HttpMethod.POST,
                URI(URIs.Users.ROOT + URIs.Users.LOGOUT),
                SirenMediaType,
                emptyList()
            )
            action(
                Rel.SEARCH_RANKING.value,
                "Search ranking",
                HttpMethod.GET,
                URI(URIs.Users.ROOT + "/ranking/1?limit=10"),
                SirenMediaType,
                listOf(
                    ActionFieldModel("ruleId", "number", "1"),
                    ActionFieldModel("search", "text"),
                    ActionFieldModel("limit", "number", "10"),
                    ActionFieldModel("offset", "number")
                )
            )
            link(URIs.HOME, Rel.SELF)
        }
}
