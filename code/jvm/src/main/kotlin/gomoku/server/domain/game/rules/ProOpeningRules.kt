package gomoku.server.domain.game.rules

import gomoku.server.domain.game.match.Color
import gomoku.server.domain.game.match.Move
import gomoku.server.domain.game.match.MoveContainer

/**
 * Represents the Pro rule set variation.
 * TODO: Implement this class
 */
data class ProOpeningRules(override val ruleId: Int, override val boardSize: BoardSize) : Rules() {
    override val variant: RuleVariant = RuleVariant.STANDARD
    override val openingRule: OpeningRule = OpeningRule.PRO

    /**
     * Checks if a move is valid according to the Pro rule set.
     * @param moveContainer The move container
     * @param move The move to check
     * @param turn The color of the player who made the move
     * @return the move result.
     */
    override fun isValidMove(moveContainer: MoveContainer, move: Move, turn: Color): IsValidMoveResult {
        TODO("Not yet implemented")
    }

    /**
     * Returns the possible moves according to the Pro rule set.
     * @param moveContainer The move container
     * @param color The color of the player
     * @return a list with the possible moves.
     */
    override fun possibleMoves(moveContainer: MoveContainer, color: Color): List<Move> {
        TODO("Not yet implemented")
    }

    /**
     * Checks if a move is a winning move according to the Pro rule set.
     * @param moveContainer The move container
     * @param move The move to check
     * @return true if the move is a winning move, false otherwise
     */
    override fun isWinningMove(moveContainer: MoveContainer, move: Move): Boolean {
        TODO("Not yet implemented")
    }
}
