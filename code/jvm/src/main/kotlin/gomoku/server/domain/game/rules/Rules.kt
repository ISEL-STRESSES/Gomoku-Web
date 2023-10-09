package gomoku.server.domain.game.rules

import gomoku.server.domain.game.board.BoardSize
import gomoku.server.domain.game.board.toBoardSize
import gomoku.server.domain.game.player.Color
import gomoku.server.domain.game.player.Move

/**
 * Represents a rule
 * @property boardSize size of the board
 * @property variant variant of the rule
 * @property openingRule opening rule
 */
sealed class Rule {
    abstract val boardSize: BoardSize
    abstract val variant: RuleVariant
    abstract val openingRule: OpeningRule

    /**
     * Checks if a move is valid based on the rules of the match
     * @param previousMoves previous moves of the match
     * @param move move to check
     * @return true if the move is valid, false otherwise
     */
    abstract fun isValidMove(previousMoves: List<Move>, move: Move): Boolean

    /**
     * Returns the possible moves based on the rules of the match
     * @param previousMoves previous moves of the match
     * @param color color of the player
     * @return the possible moves
     */
    abstract fun possibleMoves(previousMoves: List<Move>, color: Color): List<Move>

    /**
     * Checks if a move is a winning move
     * @param previousMoves previous moves of the match
     * @param move move to check
     * @return true if the move is a winning move, false otherwise
     */
    abstract fun isWinningMove(previousMoves: List<Move>, move: Move): Boolean
}

/**
 * Builds a rule based on the given parameters
 * @param boardMaxSize size of the board
 * @param variantName variant of the rule
 * @param openingRuleName opening rule
 * @return the class of the rule
 */
fun buildRule(boardMaxSize: Int, variantName: String, openingRuleName: String): Rule {
    //TODO: TAKE THIS NASTY ASS FUNCTION OUT OF HERE
    val variant = variantName.toRuleVariant()
    val openingRule = openingRuleName.toOpeningRule()
    val boardSize = boardMaxSize.toBoardSize()

    when (variant) {
        RuleVariant.STANDARD -> {
            return when (openingRule) {
                OpeningRule.FREE -> StandardRules(boardSize)
                OpeningRule.PRO -> ProOpeningRule(boardSize)
            }
        }
    }
}