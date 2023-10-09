package gomoku.server.domain.game.rules

/**
 * Represents an opening rule
 */
enum class OpeningRule {
    FREE,
    PRO;

    companion object {
        fun fromString(string: String): OpeningRule {
            return when (string) {
                "FREE" -> FREE
                "PRO" -> PRO
                else -> throw IllegalArgumentException("Unknown opening rule: $string")
            }
        }
    }
}
