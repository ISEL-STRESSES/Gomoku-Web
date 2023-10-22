package gomoku.server.http.controllers.game.models

import gomoku.server.domain.game.match.FinishedMatch

/**
 * Represents an output model for a finished match
 * to be sent from the API
 * @property id The id of the match
 * @property playerBlack The id of the black player
 * @property playerWhite The id of the white player
 * @property ruleId The id of the rule
 * @property boardSize The size of the board
 * @property openingRule The opening rule
 * @property variant The variant
 * @property moves The moves of the match
 * @property matchOutcome The outcome of the match
 */
data class FinishedMatchOutputModel(
    val id: Int,
    val playerBlack: Int,
    val playerWhite: Int,
    val ruleId: Int,
    val boardSize: Int,
    val openingRule: String,
    val variant: String,
    val moves: List<String>,
    val matchOutcome: String
) {
    companion object {

        /**
         * Creates a finished match output model from a finished match
         * @param match The finished match
         * @return The finished match output model
         * @see FinishedMatch
         */
        fun fromMatch(match: FinishedMatch): FinishedMatchOutputModel {
            return FinishedMatchOutputModel(
                id = match.id,
                playerBlack = match.playerBlack,
                playerWhite = match.playerWhite,
                ruleId = match.rules.ruleId,
                boardSize = match.rules.boardSize.value,
                openingRule = match.rules.openingRule.toString(),
                variant = match.rules.variant.toString(),
                moves = match.moveContainer.getMoves().map { it.toString() },
                matchOutcome = match.matchOutcome.toString()
            )
        }
    }
}
