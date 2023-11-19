package gomoku.server.http.responses

import gomoku.server.domain.ServerInfo
import gomoku.server.http.Rel
import gomoku.server.http.URIs
import org.springframework.http.HttpMethod
import java.net.URI

object GetHome {
    fun siren(body: ServerInfo) =
        gomoku.server.http.infra.siren(body) {
            clazz("home")
            action("signup", URI(URIs.Users.ROOT + URIs.Users.CREATE), HttpMethod.POST)
            action("login", URI(URIs.Users.ROOT + URIs.Users.TOKEN), HttpMethod.POST)
            link(URIs.HOME, Rel.SELF)
        }
}