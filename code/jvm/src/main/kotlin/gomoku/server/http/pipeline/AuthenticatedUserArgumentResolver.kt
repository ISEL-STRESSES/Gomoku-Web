package gomoku.server.http.pipeline

import gomoku.server.domain.user.AuthenticatedUser
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

/**
 * Resolves the [AuthenticatedUser] from the request
 * @see AuthenticatedUser
 * @see HandlerMethodArgumentResolver
 */
@Component
class AuthenticatedUserArgumentResolver : HandlerMethodArgumentResolver {

    /**
     * Checks if the parameter is of type [AuthenticatedUser]
     * @param parameter The parameter to check
     * @return True if the parameter is of type [AuthenticatedUser], false otherwise
     */
    override fun supportsParameter(parameter: MethodParameter) =
        parameter.parameterType == AuthenticatedUser::class.java

    /**
     * Resolves the [AuthenticatedUser] from the request
     * @param parameter The parameter to resolve
     * @param mavContainer The model and view container
     * @param webRequest The web request
     * @param binderFactory The web data binder factory
     * @return The [AuthenticatedUser] from the request
     * @throws IllegalStateException If the [AuthenticatedUser] is not found in the request
     */
    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any? {
        val request = webRequest.getNativeRequest(HttpServletRequest::class.java)
            ?: throw IllegalStateException("Failed to process the request")
        return getUserFrom(request) ?: throw IllegalStateException("User is not Authenticated")
    }

    companion object {
        private const val KEY = "AuthenticatedUserArgumentResolver"

        /**
         * Adds the [AuthenticatedUser] to the request
         * @param user The [AuthenticatedUser] to add
         * @param request The request
         */
        fun addUserTo(user: AuthenticatedUser, request: HttpServletRequest) {
            return request.setAttribute(KEY, user)
        }

        /**
         * Gets the [AuthenticatedUser] from the request
         * @param request The request
         * @return The [AuthenticatedUser] from the request
         */
        fun getUserFrom(request: HttpServletRequest): AuthenticatedUser? {
            return request.getAttribute(KEY)?.let {
                it as? AuthenticatedUser
            }
        }
    }
}
