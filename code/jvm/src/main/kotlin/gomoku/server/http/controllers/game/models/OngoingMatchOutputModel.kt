package gomoku.server.http.controllers.game.models

import gomoku.server.domain.game.match.Color
import gomoku.server.domain.game.match.OngoingMatch

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
