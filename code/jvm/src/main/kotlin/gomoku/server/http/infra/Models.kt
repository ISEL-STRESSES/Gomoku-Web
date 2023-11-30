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
data class SirenModel<T>(
    @get:JsonProperty("class")
    val clazz: List<String>,
    val properties: T,
    val links: List<LinkModel>,
    val actions: List<ActionModel>
) {
    companion object {
        const val MEDIA_TYPE = "application/vnd.siren+json"
    }
}

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
    val href: String,
    val method: String
)
