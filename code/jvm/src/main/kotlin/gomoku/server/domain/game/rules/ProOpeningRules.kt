package gomoku.server.domain.game.rules

import gomoku.server.domain.game.match.Color
import gomoku.server.domain.game.match.Move
import gomoku.server.domain.game.match.MoveContainer

data class ProOpeningRules(override val ruleId: Int, override val boardSize: BoardSize) : Rules() {
    override val variant: RuleVariant = RuleVariant.STANDARD
    override val openingRule: OpeningRule = OpeningRule.PRO
    override fun isValidMove(moveContainer: MoveContainer, move: Move, turn: Color): IsValidMoveResult {
        TODO("Not yet implemented")
    }

    override fun possibleMoves(moveContainer: MoveContainer, color: Color): List<Move> {
        TODO("Not yet implemented")
    }

    override fun isWinningMove(moveContainer: MoveContainer, move: Move): Boolean {
        TODO("Not yet implemented")
    }
}
