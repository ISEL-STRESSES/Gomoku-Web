package gomoku.server.domain.game.rules

/**
 * Represents a rule variant
 * @property ruleId The id of the rule
 * @property boardSize The size of the board
 * @property variant The variant of the rule
 * @property openingRule The opening rule of the rule
 */
data class RulesRepresentation(
    val ruleId: Int,
    val boardSize: BoardSize,
    val variant: RuleVariant,
    val openingRule: OpeningRule
)
