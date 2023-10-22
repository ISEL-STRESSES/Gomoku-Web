package gomoku.server.http.controllers.game.models

import gomoku.server.domain.game.Matchmaker

/**
 * Represents the matchmaker to be sent from the API
 * @param isMatch whether the matchmaker has found a match
 * @param id the id of the matchmaker
 */
data class MatchmakerOutputModel(
    val isMatch: Boolean,
    val id: Int
){
    constructor(matchmaker: Matchmaker) : this(
        matchmaker.isMatch,
        matchmaker.id
    )
}