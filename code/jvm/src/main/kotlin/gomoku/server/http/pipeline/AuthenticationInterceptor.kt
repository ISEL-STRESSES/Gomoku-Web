package gomoku.server.http.pipeline

import gomoku.server.domain.user.AuthenticatedUser
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor

/**
 * Interceptor that enforces authentication
 * for endpoints that require it
 * @property authorizationHeaderProcessor The [RequestTokenProcessor] to use
 * to process the authorization header
 * @see RequestTokenProcessor
 */
@Component
class AuthenticationInterceptor(
    private val authorizationHeaderProcessor: RequestTokenProcessor
) : HandlerInterceptor {

    /**
     * Pre-handle method that enforces authentication
     * for endpoints that require it
     * @param request The request
     * @param response The response
     * @param handler The handler
     * @return True if the request is valid, false otherwise
     */
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (handler is HandlerMethod && handler.methodParameters.any {
            it.parameterType == AuthenticatedUser::class.java
        }
        ) {

            // check authorization in Authorization Header
            val userFromAuthorization = authorizationHeaderProcessor
                .processAuthorizationHeaderValue(request.getHeader(NAME_AUTHORIZATION_HEADER))

            //check authorization in cookies
            val token = request.cookies?.firstOrNull { it.name == TOKEN_COOKIE }?.value
            val usernameFromCookie = request.cookies?.firstOrNull { it.name == USERNAME_COOKIE }?.value

            if (userFromAuthorization != null && token != null && usernameFromCookie != null){
                throw Exception("Both authorization header and cookies are present")
            } else if(userFromAuthorization == null && token == null && usernameFromCookie == null){
                response.status = 401
                response.addHeader(NAME_WWW_AUTHENTICATE_HEADER, TOKEN_COOKIE)
                response.addHeader(NAME_WWW_AUTHENTICATE_HEADER, RequestTokenProcessor.SCHEME)
                return false
            }

            //Handle if token is in cookies
            if (usernameFromCookie != null && token != null) {
                val userFromCookie = authorizationHeaderProcessor.userService.getUserByToken(token)
                val authUserFromCookie = userFromCookie?.let { AuthenticatedUser(it, token) }
                return if (authUserFromCookie == null) {
                    response.status = 401
                    response.addHeader(NAME_WWW_AUTHENTICATE_HEADER, TOKEN_COOKIE)
                    false
                } else {
                    AuthenticatedUserArgumentResolver.addUserTo(authUserFromCookie, request)
                    true
                }
            }

            //Handle if token is in authorization
            return if (userFromAuthorization == null) {
                response.status = 401
                response.addHeader(NAME_WWW_AUTHENTICATE_HEADER, RequestTokenProcessor.SCHEME)
                false
            } else {
                AuthenticatedUserArgumentResolver.addUserTo(userFromAuthorization, request)
                true
            }
        }
        return true
    }

    companion object {
        const val NAME_AUTHORIZATION_HEADER = "Authorization"
        private const val NAME_WWW_AUTHENTICATE_HEADER = "WWW-Authenticate"
        const val TOKEN_COOKIE = "tokenCookie"
        const val USERNAME_COOKIE = "usernameCookie"
    }
}
