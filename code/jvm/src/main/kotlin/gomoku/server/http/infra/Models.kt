package gomoku.server.http.infra

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * TODO
 */
@JvmInline
value class LinkRelation(
    val value: String
)

/**
 * TODO
 */
data class SirenModel(
    @get:JsonProperty("class")
    val clazz: List<String>,
    val properties: Any,
    val entities: List<EntityModel>,
    val actions: List<ActionModel>,
    val links: List<LinkModel>
) {
    companion object {
        const val MEDIA_TYPE = "application/vnd.siren+json"
    }
}

data class EntityModel(
    @get:JsonProperty("class")
    val clazz: List<String>,
    val rel: List<String>,
    val properties: Any,
    val links: List<LinkModel>
)

/**
 * TODO
 */
data class LinkModel(
    val rel: List<String>,
    val href: String
)

/**
 * TODO
 */
data class ActionModel(
    val name: String,
    val title: String,
    val method: String,
    val href: String,
    val type: String,
    val fields: List<Any>
)

data class ActionFieldModel(
    val name: String,
    val type: String? = null,
    val value: String? = null
)

data class PropertyRankingModel(
    val ruleId: Int,
    val size: Int
)

data class PropertyDefaultModel(
    val size: Int
)

data class PropertyUserStatsModel(
    val userId: Int,
    val username: String,
    val size: Int
)
