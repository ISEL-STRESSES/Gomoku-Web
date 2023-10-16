package gomoku.server.domain.game.rules

import gomoku.server.domain.game.match.Move
import gomoku.server.domain.game.match.MoveContainer
import gomoku.server.domain.game.player.Color
import gomoku.utils.Either

typealias IsValidMoveResult = Either<MoveError, Unit>

sealed class MoveError {
    object ImpossiblePosition : MoveError()
    object AlreadyOccupied : MoveError()
    object InvalidTurn : MoveError()
}

/**
 * Represents a rule
 * @property ruleId id of the rule
 * @property boardSize size of the board
 * @property variant variant of the rule
 * @property openingRule opening rule
 */
sealed class Rules {
    abstract val ruleId: Int
    abstract val boardSize: BoardSize
    abstract val variant: RuleVariant
    abstract val openingRule: OpeningRule

    /**
     * Checks if a move is valid based on the rules of the match
     * @param moveContainer previous moves of the match
     * @param move move to check
     * @param turn color of the player trying to play
     * @return true if the move is valid in this set of rules, false otherwise
     */
    abstract fun isValidMove(moveContainer: MoveContainer, move: Move, turn: Color): IsValidMoveResult

    /**
     * Returns the possible moves based on the rules of the match
     * @param moveContainer previous moves of the match
     * @param color color of the player
     * @return the possible moves possible in this set of rules
     */
    abstract fun possibleMoves(moveContainer: MoveContainer, color: Color): List<Move>

    /**
     * Checks if a move is a winning move
     * @param moveContainer previous moves of the match
     * @param move move to check if it was a winning move
     * @return true if the move is a winning move, false otherwise
     */
    abstract fun isWinningMove(moveContainer: MoveContainer, move: Move): Boolean
}

/**
 * Builds a rule based on the given parameters
 * @param boardMaxSize size of the board
 * @param variantName variant of the rule
 * @param openingRuleName opening rule
 * @return the class of the rule
 */
fun buildRule(ruleId: Int, boardMaxSize: Int, variantName: String, openingRuleName: String): Rules {
    val variant = variantName.toRuleVariant()
    val openingRule = openingRuleName.toOpeningRule()
    val boardSize = boardMaxSize.toBoardSize()

    when (variant) {
        RuleVariant.STANDARD -> {
            return when (openingRule) {
                OpeningRule.FREE -> StandardRules(ruleId, boardSize)
                OpeningRule.PRO -> ProOpeningRules(ruleId, boardSize)
            }
        }
    }
}
