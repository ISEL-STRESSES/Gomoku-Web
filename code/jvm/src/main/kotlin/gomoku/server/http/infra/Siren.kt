package gomoku.server.http.infra

import org.springframework.http.HttpMethod
import java.net.URI

/**
 * TODO
 */
open class SirenBuilderScope {
    private val classes = mutableListOf<String>()
    private val properties = mutableListOf<Any>()
    private val entities = mutableListOf<EntityModel>()
    private val actions = mutableListOf<ActionModel>()
    private val links = mutableListOf<LinkModel>()

    /**
     * TODO
     */
    fun clazz(value: String) {
        classes.add(value)
    }

    /**
     * TODO
     */
    fun property(value: Any) {
        properties.add(value)
    }

    /**
     * TODO
     */
    fun entity(value: EntityModel) {
        entities.add(value)
    }

    /**
     * TODO
     */
    fun action(name: String, title: String, method: HttpMethod, href: URI, type: String, fields: List<Any>) {
        val scope = ActionBuilderScope(name, title, method, href, type, fields)
        actions.add(scope.build())
    }

    /**
     * TODO
     */
    fun link(href: String, rel: LinkRelation) {
        links.add(LinkModel(listOf(rel.value), URI(href).toASCIIString()))
    }

    /**
     * TODO
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
 * TODO
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
     * TODO
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
 * TODO
 */
fun siren(block: SirenBuilderScope.() -> Unit): SirenModel {
    val scope = SirenBuilderScope()
    scope.block()
    return scope.build()
}

const val SirenMediaType = "application/vnd.siren+json"
