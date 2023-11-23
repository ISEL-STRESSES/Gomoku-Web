package gomoku.server.http.responses

import gomoku.server.domain.ServerInfo
import gomoku.server.http.Rel
import gomoku.server.http.URIs
import org.springframework.http.HttpMethod
import java.net.URI

object SirenHomeResponses {
    fun siren(body: ServerInfo) =
        gomoku.server.http.infra.siren(body) {
            clazz(Rel.HOME.value)
            action(Rel.SIGNUP.value, URI(URIs.Users.ROOT + URIs.Users.CREATE), HttpMethod.POST)
            action(Rel.LOGIN.value, URI(URIs.Users.ROOT + URIs.Users.TOKEN), HttpMethod.POST)
            link(URIs.HOME, Rel.SELF)
        }
}
