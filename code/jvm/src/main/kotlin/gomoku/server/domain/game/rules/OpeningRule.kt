package gomoku.server.domain.game.rules

/**
 * Represents an opening rule
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
    return when (this) {
        "FREE" -> OpeningRule.FREE
        "PRO" -> OpeningRule.PRO
        else -> throw IllegalArgumentException("Invalid opening rule")
    }
}
