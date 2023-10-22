package gomoku.server.http.controllers.game.models

data class GetFinishedMatchesOutputModel(
    val finishedMatches: List<MatchOutputModel>
)