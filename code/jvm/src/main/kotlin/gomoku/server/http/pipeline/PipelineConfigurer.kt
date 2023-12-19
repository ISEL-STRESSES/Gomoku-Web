package gomoku.server.http.pipeline

import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * Configures the pipeline.
 * @param authenticationInterceptor The authentication interceptor.
 * @param authenticatedUserArgumentResolver The authenticated user argument resolver.
 * @see WebMvcConfigurer
 */
@Configuration
class PipelineConfigurer(
    val authenticationInterceptor: AuthenticationInterceptor,
    val authenticatedUserArgumentResolver: AuthenticatedUserArgumentResolver
) : WebMvcConfigurer {

    /**
     * Overrides the addInterceptors method to add the authentication interceptor.
     */
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(authenticationInterceptor)
        // registry.addInterceptor(pathAuthenticationInterceptor)
    }

    /**
     * Overrides the addArgumentResolvers method to add the authenticated user argument resolver.
     */
    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(authenticatedUserArgumentResolver)
    }
}
