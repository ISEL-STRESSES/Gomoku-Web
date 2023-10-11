package gomoku.server.domain.game.rules

import gomoku.server.domain.game.player.Color
import gomoku.server.domain.game.player.Move

data class ProOpeningRules(override val boardSize: BoardSize) : Rule() {
    override val variant: RuleVariant = RuleVariant.STANDARD
    override val openingRule: OpeningRule = OpeningRule.PRO
    override fun isValidMove(previousMoves: List<Move>, move: Move): Boolean {
        TODO("Not yet implemented")
    }

    override fun possibleMoves(previousMoves: List<Move>, color: Color): List<Move> {
        TODO("Not yet implemented")
    }

    override fun isWinningMove(previousMoves: List<Move>, move: Move): Boolean {
        TODO("Not yet implemented")
    }
}
