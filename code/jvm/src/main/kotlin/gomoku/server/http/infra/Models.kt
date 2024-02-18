package gomoku.server.http.infra

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Link relations used in the API
 * @property value The value of the link relation
 */
@JvmInline
value class LinkRelation(
    val value: String
)

/**
 * Model for the Siren hypermedia format
 * @property clazz The class of the entity
 * @property properties The properties of the entity
 * @property entities The entities of the entity
 * @property actions The actions of the entity
 * @property links The links of the entity
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

/**
 * Entity model for the Siren hypermedia format
 * @property clazz The class of the entity
 * @property rel The relations of the entity
 * @property properties The properties of the entity
 * @property links The links of the entity
 */
data class EntityModel(
    @get:JsonProperty("class")
    val clazz: List<String>,
    val rel: List<String>,
    val properties: Any,
    val links: List<LinkModel>
)

/**
 * Link model for the Siren hypermedia format, it is
 * used for navigation
 * @property rel The relations of the entity
 * @property href The href of the entity
 */
data class LinkModel(
    val rel: List<String>,
    val href: String
)

/**
 * Action model for the Siren hypermedia format
 * @property name The name of the action
 * @property title The title of the action
 * @property method The HTTP method of the action
 * @property href the URI of the action
 * @property type the media type of the action
 * @property fields the fields of the action
 */
data class ActionModel(
    val name: String,
    val title: String,
    val method: String,
    val href: String,
    val type: String,
    val fields: List<Any>
)

/**
 * Action field model for the Siren hypermedia format
 * @property name The name of the action field
 * @property type The type of the action field
 * @property value The value of the action field
 */
data class ActionFieldModel(
    val name: String,
    val type: String? = null,
    val value: String? = null
)

/**
 * Model for the property of the ranking entity
 * @property ruleId The rule id of the ranking
 * @property size The size of the ranking
 */
data class PropertyRankingModel(
    val ruleId: Int,
    val size: Int
)

/**
 * Model for the property of an entity collection
 * @property size The amount of entities in the collection
 */
data class PropertyDefaultModel(
    val size: Int
)

/**
 * Model for the property of the user stats entity
 * @property userId The user id of the stats
 * @property size The size of the stats
 * @property size The size of the stats
 */
data class PropertyUserStatsModel(
    val userId: Int,
    val username: String,
    val size: Int
)
