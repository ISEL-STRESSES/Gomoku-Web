package gomoku.server.domain.game.rules

/**
 * Represents a rule variant
 * Currently this only Includes the standard variant, but it could be extended to include other variants
 */
enum class RuleVariant {
    STANDARD
}

/**
 * Helper function to deserialize a rule variant from a string
 * @receiver the string to deserialize
 * @return the rule variant
 */
fun String.toRuleVariant(): RuleVariant {
    return when (this.uppercase()) {
        "STANDARD" -> RuleVariant.STANDARD
        else -> throw IllegalArgumentException("Invalid rule variant")
    }
}
