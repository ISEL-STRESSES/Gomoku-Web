package gomoku.server.domain.game.rules

/**
 * Represents a rule variant
 * Currently this only Includes the default variant but leaves space for future growth.
 */
enum class RuleVariant {
    STANDARD;

    companion object {
        fun fromString(string: String): RuleVariant {
            return when (string) {
                "STANDARD" -> STANDARD
                else -> throw IllegalArgumentException("Unknown rule variant: $string")
            }
        }
    }
}
