package gomoku.server.http.infra

import gomoku.server.http.Rel
import org.springframework.http.HttpMethod
import java.net.URI

/**
 * TODO
 */
open class SirenBuilderScope<T>(
    val properties: T
) {
    private val links = mutableListOf<LinkModel>()
    private val classes = mutableListOf<String>()
    private val actions = mutableListOf<ActionModel>()

    /**
     * TODO
     */
    fun clazz(value: String) {
        classes.add(value)
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
    fun action(name: String, href: URI, method: HttpMethod) {
        val scope = ActionBuilderScope(name, href, method)
        actions.add(scope.build())
    }

    /**
     * TODO
     */
    fun build(): SirenModel<T> = SirenModel(
        clazz = classes,
        properties = properties,
        links = links,
        actions = actions
    )
}

/**
 * TODO
 */
class ActionBuilderScope(
    private val name: String,
    private val href: URI,
    private val method: HttpMethod
) {
    /**
     * TODO
     */
    fun build() = ActionModel(name, href.toASCIIString(), method.name())
}

/**
 * TODO
 */
fun <T> siren(value: T, block: SirenBuilderScope<T>.() -> Unit): SirenModel<T> {
    val scope = SirenBuilderScope(value)
    scope.block()
    return scope.build()
}

/**
 * TODO
 */
fun <T> makeSiren(body: T, link: String) =
    siren(body) {
        link(link, Rel.SELF)
        link(link, LinkRelation(link))
    }
