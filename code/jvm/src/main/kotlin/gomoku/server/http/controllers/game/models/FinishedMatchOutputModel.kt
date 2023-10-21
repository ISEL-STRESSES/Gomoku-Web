package gomoku.server.http.controllers.game.models

import gomoku.server.domain.game.match.FinishedMatch

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
