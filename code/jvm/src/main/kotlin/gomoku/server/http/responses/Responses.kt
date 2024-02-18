package gomoku.server.http.responses

import gomoku.server.http.infra.SirenModel
import org.springframework.http.ResponseEntity

/**
 * Creates a response with the Siren model
 * @receiver The Siren model to be sent
 * @param status The HTTP status code
 * @see ResponseEntity
 */
fun SirenModel.response(status: Int) = ResponseEntity
    .status(status)
    .header("Content-Type", SirenModel.MEDIA_TYPE)
    .body(this)

/**
 * Creates a response with the Siren model and redirects to the given URI
 * @receiver The Siren model to be sent
 * @param status The HTTP status code
 * @param headerValues The URI to be redirected to
 * @see ResponseEntity
 */
fun SirenModel.responseRedirect(status: Int, headerValues: String) = ResponseEntity
    .status(status)
    .header("Content-Type", SirenModel.MEDIA_TYPE)
    .header("Location", headerValues)
    .body(this)
