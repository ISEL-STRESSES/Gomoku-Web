package gomoku.server.http.controllers.media

import org.springframework.http.ResponseEntity
import java.net.URI

class Problem(
    typeUri: URI
) {
    val type = typeUri.toASCIIString()

    companion object {
        const val MEDIA_TYPE = "application/problem+json"
        private const val PROBLEM_BASE_URL = "https://github.com/isel-leic-daw/2023-daw-leic51d-01/main/docs/problems/"
        fun response(status: Int, problem: Problem) = ResponseEntity
            .status(status)
            .header("Content-Type", MEDIA_TYPE)
            .body<Any>(problem)

        val userAlreadyExists = Problem(
            URI(
                PROBLEM_BASE_URL + "user-already-exists"
            )
        )
        val insecurePassword = Problem(
            URI(
                PROBLEM_BASE_URL + "insecure-password"
            )
        )
        val invalidUsername = Problem(
            URI(
                PROBLEM_BASE_URL + "invalid-username"
            )
        )
        val userOrPasswordAreInvalid = Problem(
            URI(
                PROBLEM_BASE_URL + "user-or-password-are-invalid"
            )
        )

        val invalidRequestContent = Problem(
            URI(
                PROBLEM_BASE_URL + "invalid-request-content"
            )
        )

        val userNotFound = Problem(
            URI(
                PROBLEM_BASE_URL + "user-not-found"
            )
        )

        val gameNotFound = Problem(
            URI(
                PROBLEM_BASE_URL + "game-not-found"
            )
        )

        val positionOccupied = Problem(
            URI(
                PROBLEM_BASE_URL + "position-occupied"
            )
        )

        val notYourTurn = Problem(
            URI(
                PROBLEM_BASE_URL + "not-your-turn"
            )
        )

        val gameAlreadyFinished = Problem(
            URI(
                PROBLEM_BASE_URL + "game-already-finished"
            )
        )

        val impossiblePosition = Problem(
            URI(
                PROBLEM_BASE_URL + "impossible-position"
            )
        )

        // kabbom TODO
        val makeMoveFailed = Problem(
            URI(
                PROBLEM_BASE_URL + "make-move-failed"
            )
        )

        val samePlayer = Problem(
            URI(
                PROBLEM_BASE_URL + "same-player"
            )
        )

        val lobbyNotFound = Problem(
            URI(
                PROBLEM_BASE_URL + "lobby-not-found"
            )
        )
    }
}
