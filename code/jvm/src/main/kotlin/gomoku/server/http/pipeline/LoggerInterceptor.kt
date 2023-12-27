package gomoku.server.http.pipeline

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView

@Component
class LoggerInterceptor : HandlerInterceptor {
    @Throws(Exception::class)
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        logger.info("Before handling the request")
        logger.info("Request URL: ${request.requestURL}")
        logger.info("Request URI: ${request.method}")
        logger.info("Response: $response")
        logger.info("Handler: $handler")
        return true
    }

    @Throws(Exception::class)
    override fun postHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        modelAndView: ModelAndView?
    ) {
        logger.info("After handling the request")
        logger.info("Request: $request")
        logger.info("Response: $response")
        logger.info("Handler: $handler")
    }

    @Throws(Exception::class)
    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?
    ) {
        logger.info("After completing the request and response cycle")
        logger.info("Request: $request")
        logger.info("Response: $response")
        logger.info("Handler: $handler")
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(LoggerInterceptor::class.java)
    }
}
