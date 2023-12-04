package gomoku.server.http.responses

import gomoku.server.http.infra.SirenModel
import org.springframework.http.ResponseEntity

/**
 * TODO
 */
fun SirenModel.response(status: Int) = ResponseEntity
    .status(status)
    .header("Content-Type", SirenModel.MEDIA_TYPE)
    .body(this)

/**
 * TODO
 */
fun SirenModel.responseRedirect(status: Int, headerValues: String) = ResponseEntity
    .status(status)
    .header("Content-Type", SirenModel.MEDIA_TYPE)
    .header("Location", headerValues)
    .body(this)
