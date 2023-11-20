package gomoku.server.http.responses

import gomoku.server.http.infra.SirenModel
import org.springframework.http.ResponseEntity

fun <T> SirenModel<T>.response(status: Int) = ResponseEntity
    .status(status)
    .header("Content-Type", SirenModel.MEDIA_TYPE)
    .body(this)

fun <T> SirenModel<T>.responseRedirect(status: Int, headerValues: String) = ResponseEntity
    .status(status)
    .header("Content-Type", SirenModel.MEDIA_TYPE)
    .header("Location", headerValues)
    .body(this)
