package gomoku.server.http.infra

import org.springframework.http.HttpMethod
import java.net.URI

/**
 * Builder scope for the Siren hypermedia entities
 */
open class SirenBuilderScope {
    private val classes = mutableListOf<String>()
    private lateinit var properties: Any
    private val entities = mutableListOf<EntityModel>()
    private val actions = mutableListOf<ActionModel>()
    private val links = mutableListOf<LinkModel>()

    /**
     * Adds a class to the entity
     * @param value The class to be added
     */
    fun clazz(value: String) {
        classes.add(value)
    }

    /**
     * Adds a property to the entity
     * @param value The property to be added
     */
    fun property(value: Any) {
        properties = value
    }

    /**
     * Adds a sub-entity to the entity
     * @param value The sub-entity to be added
     */
    fun entity(value: EntityModel) {
        entities.add(value)
    }

    /**
     * Adds an action to the entity
     * @param name The name of the action
     * @param title The title of the action
     * @param method The HTTP method of the action
     * @param href The URI of the action
     * @param type The media type of the action
     * @param fields The fields of the action
     */
    fun action(name: String, title: String, method: HttpMethod, href: URI, type: String, fields: List<Any>) {
        val scope = ActionBuilderScope(name, title, method, href, type, fields)
        actions.add(scope.build())
    }

    /**
     * Adds a link to the entity
     * @param href The URI of the link
     * @param rel The relation of the link
     */
    fun link(href: String, rel: LinkRelation) {
        links.add(LinkModel(listOf(rel.value), URI(href).toASCIIString()))
    }

    /**
     * Builds the entity based on the scope
     */
    fun build() = SirenModel(
        classes,
        properties,
        entities,
        actions,
        links
    )
}

/**
 * Builder scope for the Siren hypermedia actions
 */
class ActionBuilderScope(
    private val name: String,
    private val title: String,
    private val method: HttpMethod,
    private val href: URI,
    private val type: String,
    private val fields: List<Any>
) {
    /**
     * Builds the action based on the scope
     */
    fun build() = ActionModel(
        name,
        title,
        method.name(),
        href.toASCIIString(),
        type,
        fields
    )
}

/**
 * Creates a Siren hypermedia entity
 * @param block The builder scope
 */
fun siren(block: SirenBuilderScope.() -> Unit): SirenModel {
    val scope = SirenBuilderScope()
    scope.block()
    return scope.build()
}

const val SirenMediaType = "application/vnd.siren+json"
