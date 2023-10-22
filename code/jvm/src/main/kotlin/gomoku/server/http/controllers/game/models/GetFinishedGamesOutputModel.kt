package gomoku.server.http.controllers.game.models

/**
 * Represents the finished games to be sent from the API
 * @param finishedGames the finished games
 */
data class GetFinishedGamesOutputModel(
    val finishedGames: List<GameOutputModel>
)
