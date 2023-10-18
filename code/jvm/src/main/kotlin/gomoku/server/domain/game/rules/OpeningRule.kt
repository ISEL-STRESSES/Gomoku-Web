package gomoku.server.domain.game.rules

/**
 * Represents an opening rule
 * @property FREE the opening rule is free
 * @property PRO the opening rule is pro
 */
enum class OpeningRule {
    FREE,
    PRO
}

/**
 * Helper function to deserialize an opening rule from a string
 * @receiver the string to deserialize
 * @return the opening rule
 */
fun String.toOpeningRule(): OpeningRule {
    return when (this.uppercase()) {
        "FREE" -> OpeningRule.FREE
        "PRO" -> OpeningRule.PRO
        else -> throw IllegalArgumentException("Invalid opening rule")
    }
}
