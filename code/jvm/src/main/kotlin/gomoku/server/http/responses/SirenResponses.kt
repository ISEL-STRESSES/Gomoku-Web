package gomoku.server.http.responses

import gomoku.server.domain.ServerInfo
import gomoku.server.http.Rel
import gomoku.server.http.URIs
import gomoku.server.http.infra.siren
import org.springframework.http.HttpMethod
import java.net.URI

object GetHome {
    fun siren(body: ServerInfo) =
        siren(body) {
            clazz("home")
            action("signup", URI(URIs.Users.CREATE), HttpMethod.POST)
            action("login", URI(URIs.Users.TOKEN), HttpMethod.POST)
            link(URIs.HOME, Rel.SELF)
        }
}

// TODO: Add the remaining responses