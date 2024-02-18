package gomoku.server.http.controllers.game.models

import gomoku.server.domain.game.Matchmaker

/**
 * Represents the matchmaker to be sent from the API
 * @param isGame whether the matchmaker has found a game
 * @param id the id of the matchmaker
 */
data class MatchmakerOutputModel(
    val isGame: Boolean,
    val id: Int
) {
    constructor(matchmaker: Matchmaker) : this(
        matchmaker.isGame,
        matchmaker.id
    )
}
