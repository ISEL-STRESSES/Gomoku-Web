package gomoku.server.http.controllers.game.models

import gomoku.server.domain.game.match.Color
import gomoku.server.domain.game.match.OngoingMatch

/**
 * Represents an output model for an ongoing match
 * to be sent from the API
 * @property id The id of the match
 * @property playerBlack The id of the black player
 * @property playerWhite The id of the white player
 * @property ruleId The id of the rule
 * @property boardSize The size of the board
 * @property openingRule The opening rule
 * @property variant The variant
 * @property moves The moves of the match
 * @property turn The color of the player whose turn it is
 */
class OngoingMatchOutputModel(
    val id: Int,
    val playerBlack: Int,
    val playerWhite: Int,
    val ruleId: Int,
    val boardSize: Int,
    val openingRule: String,
    val variant: String,
    val moves: List<String>,
    val turn: Color
) {
    companion object {

        /**
         * Creates an ongoing match output model from an ongoing match
         * @param match The ongoing match
         * @return The ongoing match output model
         * @see OngoingMatch
         */
        fun fromMatch(match: OngoingMatch): OngoingMatchOutputModel {
            return OngoingMatchOutputModel(
                id = match.id,
                playerBlack = match.playerBlack,
                playerWhite = match.playerWhite,
                ruleId = match.rules.ruleId,
                boardSize = match.rules.boardSize.value,
                openingRule = match.rules.openingRule.name,
                variant = match.rules.variant.toString(),
                moves = match.moveContainer.getMoves().map { it.toString() },
                turn = match.turn
            )
        }
    }
}
