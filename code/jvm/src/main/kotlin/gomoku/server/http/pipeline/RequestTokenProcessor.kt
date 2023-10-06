package gomoku.server.http.pipeline

import gomoku.server.domain.user.AuthenticatedUser
import gomoku.server.services.user.UserService
import org.springframework.stereotype.Component

@Component
class RequestTokenProcessor(
    val userService: UserService
) {

    fun processAuthorizationHeaderValue(authorizationValue: String?): AuthenticatedUser? {
        if (authorizationValue == null) {
            return null
        }
        val parts = authorizationValue.trim().split(" ")
        if (parts.size != 2) {
            return null
        }
        if (parts[0].lowercase() != SCHEME) {
            return null
        }
        return userService.getUserByToken(parts[1])?.let {
            AuthenticatedUser(
                it,
                parts[1]
            )
        }
    }

    companion object {
        const val SCHEME = "bearer"
    }
}
