package gomoku.server.http.controllers.game.models

import gomoku.server.domain.game.match.Color
import gomoku.server.domain.game.match.FinishedMatch
import gomoku.server.domain.game.match.Match
import gomoku.server.domain.game.match.OngoingMatch

data class MatchOutputModel(
    val id: Int,
    val playerBlack: Int,
    val playerWhite: Int,
    val rule: RuleOutputModel,
    val moves: MoveContainerOutputModel,
    val matchOutcome: String?,
    val turn: Color?,
    val type: MatchType
) {

    constructor(finishedMatch: FinishedMatch) : this(
        id = finishedMatch.id,
        playerBlack = finishedMatch.playerBlack,
        playerWhite = finishedMatch.playerWhite,
        rule = RuleOutputModel(finishedMatch.rules),
        moves = MoveContainerOutputModel(finishedMatch.moveContainer),
        matchOutcome = finishedMatch.matchOutcome.toString(),
        turn = null,
        type = MatchType.FINISHED
    )

    constructor(match: OngoingMatch): this(
        id = match.id,
        playerBlack = match.playerBlack,
        playerWhite = match.playerWhite,
        rule = RuleOutputModel(match.rules),
        moves = MoveContainerOutputModel(match.moveContainer),
        matchOutcome = null,
        turn = match.turn,
        type = MatchType.ONGOING
    )

    companion object {
        fun fromMatch(match: Match): MatchOutputModel {
            return when (match) {
                is OngoingMatch -> MatchOutputModel(match)
                is FinishedMatch -> MatchOutputModel(match)
            }
        }
    }
}

enum class MatchType {
    ONGOING, FINISHED
}