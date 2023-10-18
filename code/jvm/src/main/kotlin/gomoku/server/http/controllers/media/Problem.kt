package gomoku.server.http.controllers.media

import org.springframework.http.ResponseEntity
import java.net.URI

// TODO: ADD THE PROBLEMS THAT ARE NOT ALREADY IN THE DOCS TO THE DOCS!

/**
 * Represents a problem
 * @param typeUri The type of the problem
 */
class Problem(
    typeUri: URI
) {
    val type = typeUri.toASCIIString() // TODO what for?

    companion object {
        const val MEDIA_TYPE = "application/problem+json"
        private const val PROBLEM_BASE_URL = "https://github.com/isel-leic-daw/2023-daw-leic51d-01/main/docs/problems/"

        /**
         * Creates a response entity with the given status and problem
         * @param status The status of the response
         * @param problem The problem
         * @return The response entity
         */
        fun response(status: Int, problem: Problem) = ResponseEntity
            .status(status)
            .header("Content-Type", MEDIA_TYPE)
            .body<Any>(problem)

        val invalidRule = Problem(URI(PROBLEM_BASE_URL + "invalid-rule"))

        val userAlreadyExists = Problem(URI(PROBLEM_BASE_URL + "user-already-exists"))

        val insecurePassword = Problem(URI(PROBLEM_BASE_URL + "insecure-password"))

        val invalidUsername = Problem(URI(PROBLEM_BASE_URL + "invalid-username"))

        val userOrPasswordAreInvalid = Problem(URI(PROBLEM_BASE_URL + "user-or-password-are-invalid"))

        val invalidRequestContent = Problem(URI(PROBLEM_BASE_URL + "invalid-request-content"))

        val userNotFound = Problem(URI(PROBLEM_BASE_URL + "user-not-found"))

        val gameNotFound = Problem(URI(PROBLEM_BASE_URL + "game-not-found"))

        val positionOccupied = Problem(URI(PROBLEM_BASE_URL + "position-occupied"))

        val notYourTurn = Problem(URI(PROBLEM_BASE_URL + "not-your-turn"))

        val gameAlreadyFinished = Problem(URI(PROBLEM_BASE_URL + "game-already-finished"))

        val impossiblePosition = Problem(URI(PROBLEM_BASE_URL + "impossible-position"))

        val makeMoveFailed = Problem(URI(PROBLEM_BASE_URL + "make-move-failed"))

        val samePlayer = Problem(URI(PROBLEM_BASE_URL + "same-player"))

        val lobbyNotFound = Problem(URI(PROBLEM_BASE_URL + "lobby-not-found"))

        val leaveLobbyFailed = Problem(URI(PROBLEM_BASE_URL + "leave-lobby-failed"))
    }
}
