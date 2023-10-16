package gomoku.server.domain.game.rules

import gomoku.server.domain.game.player.Color
import gomoku.server.domain.game.match.Move
import gomoku.server.domain.game.match.MoveContainer

data class ProOpeningRules(override val boardSize: BoardSize) : Rules() {
    override val variant: RuleVariant = RuleVariant.STANDARD
    override val openingRule: OpeningRule = OpeningRule.PRO
    override fun isValidMove(moveContainer: List<Move>, move: Move): IsValidMoveResult {
        TODO("Not yet implemented")
    }

    override fun possibleMoves(previousMoves: List<Move>, color: Color): List<Move> {
        TODO("Not yet implemented")
    }

    override fun isWinningMove(previousMoves: List<Move>, move: Move): Boolean {
        TODO("Not yet implemented")
    }
}
