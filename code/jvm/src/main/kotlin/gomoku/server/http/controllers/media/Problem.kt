package gomoku.server.http.controllers.media

import org.springframework.http.ResponseEntity
import java.net.URI


/**
 * Represents a problem
 * @param status The status of the problem
 * @param typeUri The type of the problem
 * @param title The title of the problem
 */
class Problem(val status: Int, typeUri: URI, val title: String) {

    val type: String = typeUri.toASCIIString()


    companion object {
        private const val MEDIA_TYPE = "application/problem+json"
        private const val PROBLEM_BASE_URL = "https://github.com/isel-leic-daw/2023-daw-leic51d-01/blob/main/docs/problems/"

        /**
         * Creates a response entity with the given status and problem
         * @param problem The problem
         * @return The response entity
         */
        fun response(problem: Problem) = ResponseEntity
            .status(problem.status)
            .header("Content-Type", MEDIA_TYPE)
            .body<Any>(problem)

        val ruleNotFound = Problem(404, URI(PROBLEM_BASE_URL + "invalid-rule"), "Rule not found.")

        val userAlreadyExists = Problem(409, URI(PROBLEM_BASE_URL + "user-already-exists"), "User already exists.")

        val insecurePassword = Problem(400, URI(PROBLEM_BASE_URL + "insecure-password"), "The provided password is insecure.")

        val invalidUsername = Problem(400, URI(PROBLEM_BASE_URL + "invalid-username"), "Invalid username format.")

        val userOrPasswordAreInvalid = Problem(400, URI(PROBLEM_BASE_URL + "user-or-password-are-invalid"), "User or password are invalid.")

        val invalidRequestContent = Problem(400, URI(PROBLEM_BASE_URL + "invalid-request-content"), "Invalid request content.")

        val userNotFound = Problem(404, URI(PROBLEM_BASE_URL + "user-not-found"), "User not found.")

        val gameNotFound = Problem(404, URI(PROBLEM_BASE_URL + "game-not-found"), "Game not found.")

        val noRulesFound = Problem(404, URI(PROBLEM_BASE_URL + "rules-not-found"), "No rules found.")

        val positionOccupied = Problem(409, URI(PROBLEM_BASE_URL + "position-occupied"), "The position is already occupied.")

        val notYourTurn = Problem(400, URI(PROBLEM_BASE_URL + "not-your-turn"), "It's not your turn.")

        val gameAlreadyFinished = Problem(400, URI(PROBLEM_BASE_URL + "game-already-finished"), "Game has already finished.")

        val impossiblePosition = Problem(400, URI(PROBLEM_BASE_URL + "impossible-position"), "The chosen position is impossible.")

        val invalidMove = Problem(400, URI(PROBLEM_BASE_URL + "invalid-move"), "Invalid move.")

        val makeMoveFailed = Problem(500, URI(PROBLEM_BASE_URL + "make-move-failed"), "Failed to make a move.")

        val samePlayer = Problem(400, URI(PROBLEM_BASE_URL + "same-player"), "It's the same player.")

        val lobbyNotFound = Problem(404, URI(PROBLEM_BASE_URL + "lobby-not-found"), "Lobby not found.")

        val leaveLobbyFailed = Problem(500, URI(PROBLEM_BASE_URL + "leave-lobby-failed"), "Failed to leave the lobby.")

        val tokenNotRevoked = Problem(403, URI(PROBLEM_BASE_URL + "token-not-revoked"), "The token was not revoked.")

        val userStatsNotFound = Problem(404, URI(PROBLEM_BASE_URL + "user-stats-not-found"), "User stats not found.")
    }
}
