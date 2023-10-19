package gomoku.server.http

import gomoku.server.http.controllers.media.Problem
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

/**
 * Exception handler for the application
 * @see ResponseEntityExceptionHandler
 */
@ControllerAdvice
class ExceptionHandler : ResponseEntityExceptionHandler() {

    /**
     * Handles [MethodArgumentNotValidException]s
     * @param ex The exception
     * @param headers The headers
     * @param status The status
     * @param request The request
     * @return A response entity with a problem
     */
    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        log.info("Handling MethodArgumentNotValidException: {}", ex.message)
        return Problem.response(400, Problem.invalidRequestContent)
    }

    /**
     * Handles [HttpMessageNotReadableException]s
     * @param ex The exception
     * @param headers The headers
     * @param status The status
     * @param request The request
     * @return A response entity with a problem
     */
    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> {
        log.info("Handling HttpMessageNotReadableException: {}", ex.httpInputMessage)
        return Problem.response(400, Problem.invalidRequestContent)
    }

    /**
     * Handles [Exception]s
     * @return A response entity with a problem
     */
    @ExceptionHandler(Exception::class)
    fun handleAll(ex: Exception): ResponseEntity<Unit> {
        log.error("Exception caught: ", ex)
        return ResponseEntity.status(500).build()
    }

    companion object {
        private val log = LoggerFactory.getLogger(ExceptionHandler::class.java)
    }
}
