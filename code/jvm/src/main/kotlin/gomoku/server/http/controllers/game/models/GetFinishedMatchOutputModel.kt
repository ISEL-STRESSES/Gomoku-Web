package gomoku.server.http.controllers.game.models

/**
 * Represents the finished matches to be sent from the API
 * @param finishedMatches the finished matches
 */
data class GetFinishedMatchesOutputModel(
    val finishedMatches: List<MatchOutputModel>
)