package gomoku.server.http.controllers.media

import org.springframework.http.ResponseEntity
import java.net.URI

class Problem(
    typeUri: URI
) {
    val type = typeUri.toASCIIString()

    companion object {
        const val MEDIA_TYPE = "application/problem+json"
        fun response(status: Int, problem: Problem) = ResponseEntity
            .status(status)
            .header("Content-Type", MEDIA_TYPE)
            .body<Any>(problem)

        val userAlreadyExists = Problem(
            URI(
                "TODO:"
            )
        )
        val insecurePassword = Problem(
            URI(
                "TODO:"
            )
        )
        val invalidUsername = Problem(
            URI(
                "TODO:"
            )
        )
        val userOrPasswordAreInvalid = Problem(
            URI(
                "TODO:"
            )
        )

        val invalidRequestContent = Problem(
            URI(
                "TODO:"
            )
        )

        val userNotFound = Problem(
            URI(
                "TODO:"
            )
        )
    }
}
