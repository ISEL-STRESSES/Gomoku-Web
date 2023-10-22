package gomoku.server.http.controllers.game.models

import gomoku.server.domain.game.Matchmaker

data class MatchmakerOutputModel(
    val isMatch: Boolean,
    val id: Int
){
    constructor(matchmaker: Matchmaker) : this(
        matchmaker.isMatch,
        matchmaker.id
    )
}