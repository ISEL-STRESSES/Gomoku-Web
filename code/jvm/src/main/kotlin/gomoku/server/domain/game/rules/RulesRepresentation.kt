package gomoku.server.domain.game.rules

data class RulesRepresentation (
    val ruleId: Int,
    val boardSize: BoardSize,
    val variant: RuleVariant,
    val openingRule: OpeningRule
)