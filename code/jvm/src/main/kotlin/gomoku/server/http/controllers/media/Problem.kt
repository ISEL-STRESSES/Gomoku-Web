package gomoku.server.http.controllers.media

import org.springframework.http.ResponseEntity
import java.net.URI

/**
 * Represents a problem
 * @param typeUri The type of the problem
 * @param title The title of the problem
 */
class Problem(typeUri: URI, val title: String) {

    val type: String = typeUri.toASCIIString()

    companion object {
        private const val MEDIA_TYPE = "application/problem+json"
        private const val PROBLEM_BASE_URL = "https://github.com/isel-leic-daw/2023-daw-leic51d-01/blob/main/docs/problems/"

        /**
         * Creates a response entity with the given status and problem
         * @param problem The problem
         * @return The response entity
         */
        fun response(status: Int, problem: Problem) = ResponseEntity
            .status(status)
            .header("Content-Type", MEDIA_TYPE)
            .body<Any>(problem)

        val ruleNotFound = Problem(URI(PROBLEM_BASE_URL + "invalid-rule"), "Rule not found.")

        val userAlreadyExists = Problem(URI(PROBLEM_BASE_URL + "user-already-exists"), "User already exists.")

        val insecurePassword = Problem(URI(PROBLEM_BASE_URL + "insecure-password"), "The provided password is insecure.")

        val invalidUsername = Problem(URI(PROBLEM_BASE_URL + "invalid-username"), "Invalid username format.")

        val userOrPasswordAreInvalid = Problem(URI(PROBLEM_BASE_URL + "user-or-password-are-invalid"), "User or password are invalid.")

        val invalidRequestContent = Problem(URI(PROBLEM_BASE_URL + "invalid-request-content"), "Invalid request content.")

        val userNotFound = Problem(URI(PROBLEM_BASE_URL + "user-not-found"), "User not found.")

        val gameNotFound = Problem(URI(PROBLEM_BASE_URL + "game-not-found"), "Game not found.")

        val noRulesFound = Problem(URI(PROBLEM_BASE_URL + "rules-not-found"), "No rules found.")

        val positionOccupied = Problem(URI(PROBLEM_BASE_URL + "position-occupied"), "The position is already occupied.")

        val notYourTurn = Problem(URI(PROBLEM_BASE_URL + "not-your-turn"), "It's not your turn.")

        val gameAlreadyFinished = Problem(URI(PROBLEM_BASE_URL + "game-already-finished"), "Game has already finished.")

        val impossiblePosition = Problem(URI(PROBLEM_BASE_URL + "impossible-position"), "The chosen position is impossible.")

        val invalidMove = Problem(URI(PROBLEM_BASE_URL + "invalid-move"), "Invalid move.")

        val makeMoveFailed = Problem(URI(PROBLEM_BASE_URL + "make-move-failed"), "Failed to make a move.")

        val samePlayer = Problem(URI(PROBLEM_BASE_URL + "same-player"), "It's the same player.")

        val lobbyNotFound = Problem(URI(PROBLEM_BASE_URL + "lobby-not-found"), "Lobby not found.")

        val leaveLobbyFailed = Problem(URI(PROBLEM_BASE_URL + "leave-lobby-failed"), "Failed to leave the lobby.")

        val tokenNotRevoked = Problem(URI(PROBLEM_BASE_URL + "token-not-revoked"), "The token was not revoked.")

        val userStatsNotFound = Problem(URI(PROBLEM_BASE_URL + "user-stats-not-found"), "User stats not found.")

        val playerNotInGame = Problem(URI(PROBLEM_BASE_URL + "player-not-in-game"), "Player not in game.")
    }
}
