package gomoku.server.http.controllers.game.models

import gomoku.server.domain.game.match.Color
import gomoku.server.domain.game.match.FinishedMatch
import gomoku.server.domain.game.match.Match
import gomoku.server.domain.game.match.OngoingMatch

/**
 * Represents the output model of a match to be sent from the API
 * @param id the id of the match
 * @param playerBlack the id of the black player
 * @param playerWhite the id of the white player
 * @param rule the rules of the match
 * @param moves the moves of the match
 * @param matchOutcome the outcome of the match
 * @param turn the color of the player whose turn it is
 * @param type the type of the match
 */
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
        /**
         * Resolves the representation of a match to be sent from the API
         * @param match the match to be resolved
         * @return the representation of the match
         */
        fun fromMatch(match: Match): MatchOutputModel {
            return when (match) {
                is OngoingMatch -> MatchOutputModel(match)
                is FinishedMatch -> MatchOutputModel(match)
            }
        }
    }
    
    /**
     * Represents the type of match either ongoing or finished
     */
    enum class MatchType {
        ONGOING, FINISHED
    }
}

