package gomoku.server.domain

/**
 * Represents a rule variant
 */
enum class RuleVariant {
    STANDARD,
    RENJU,
    PRO;

    companion object {
        fun fromString(string: String): RuleVariant {
            return when (string) {
                "STANDARD" -> STANDARD
                "RENJU" -> RENJU
                "PRO" -> PRO
                else -> throw IllegalArgumentException("Unknown rule variant: $string")
            }
        }
    }
}

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

/**
 * Represents a rule
 * @property boardSize size of the board
 * @property variant variant of the rule
 * @property openingRule opening rule
 */
open class Rule(val boardSize: Int, val variant: RuleVariant, val openingRule: OpeningRule) {
    init {
        check(boardSize.isBoardSizeValid())
    }

    private fun Int.isBoardSizeValid(): Boolean = this == 15 || this == 19
}

/**
 * Represents a default rule
 * @constructor creates a default rule with board size 15, standard variant and free opening rule
 */
class DefaultRule : Rule(15, RuleVariant.STANDARD, OpeningRule.FREE)

/**
 * Represents the statistics of a rule
 * @property rule rule
 * @property gamesPlayed number of games played with this rule
 * @property elo elo of the rule
 */
class RuleStats(val rule: Rule, val gamesPlayed: Int, val elo: Int)
