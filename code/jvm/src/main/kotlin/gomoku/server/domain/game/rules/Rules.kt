package gomoku.server.domain.game.rules

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import gomoku.server.domain.game.IsValidMoveResult
import gomoku.server.domain.game.game.CellColor
import gomoku.server.domain.game.game.move.Move
import gomoku.server.domain.game.game.move.MoveContainer
import gomoku.server.domain.game.game.move.Position

/**
 * Represents a rule
 * @property ruleId id of the rule
 * @property boardSize size of the board
 * @property variant variant of the rule
 * @property openingRule opening rule
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = StandardRules::class, name = "StandardRules"),
    JsonSubTypes.Type(value = ProOpeningRules::class, name = "ProOpeningRules")
)
sealed class Rules {
    abstract val ruleId: Int
    abstract val boardSize: BoardSize
    abstract val variant: RuleVariant
    abstract val openingRule: OpeningRule

    /**
     * Checks if a move is valid based on the rules of the game
     * @param moveContainer previous moves of the game
     * @param move move to check
     * @param turn color of the player trying to play
     * @return the move result
     */
    abstract fun isValidMove(moveContainer: MoveContainer, move: Move, turn: CellColor): IsValidMoveResult

    /**
     * Returns the possible moves based on the rules of the game
     * @param moveContainer previous moves of the game
     * @param cellColor color of the player
     * @return the possible moves possible in the set of rules
     */
    abstract fun possiblePositions(moveContainer: MoveContainer, cellColor: CellColor, turn: CellColor): List<Position>

    /**
     * Checks if a move is a winning move
     * @param moveContainer previous moves of the game
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
    val variant = RuleVariant.valueOf(variantName)
    val openingRule = OpeningRule.valueOf(openingRuleName)
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
