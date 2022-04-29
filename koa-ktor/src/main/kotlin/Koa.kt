package guru.zoroark.koa.ktor

import guru.zoroark.koa.dsl.KoaDslContext
import guru.zoroark.koa.dsl.RootBuilder
import guru.zoroark.koa.dsl.RootDsl
import io.ktor.application.Application
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.ApplicationFeature
import io.ktor.application.feature
import io.ktor.routing.Route
import io.ktor.util.AttributeKey
import io.swagger.v3.core.util.Json
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.Paths
import io.swagger.v3.oas.models.media.Schema

class Koa(config: Configuration) {
    private val pathItems = mutableMapOf<String, PathItem>()
    private val openApiBaseBuilder: RootBuilder = config.builder
    private val storedSchemas = mutableMapOf<String, Schema<*>>()
    private val hooks = mutableMapOf<Route, MutableList<DescriptionHook>>()

    class Configuration(internal val builder: RootBuilder = RootBuilder()) : RootDsl by builder

    companion object Feature : ApplicationFeature<ApplicationCallPipeline, Configuration, Koa> {
        override val key = AttributeKey<Koa>("Koa")

        override fun install(pipeline: ApplicationCallPipeline, configure: Configuration.() -> Unit): Koa =
                Koa(Configuration().apply(configure))
    }

    fun addOperation(path: String, method: PathItem.HttpMethod, operation: Operation) {
        pathItems.getOrPut(path) { PathItem() }.operation(method, operation)
    }

    fun buildOpenApiObject(): OpenAPI {
        return openApiBaseBuilder.build().apply {
            paths = Paths().apply {
                pathItems.forEach { (k, v) -> addPathItem(k, v) }
            }
            if (components == null) components = Components()
            components.apply {
                for ((schemaName, schema) in storedSchemas)
                    addSchemas(schemaName, schema)
            }
        }
    }

    fun buildOpenApiDocument(): String {
        // TODO most of this could be made a one-time lazy operation?
        val openApi = buildOpenApiObject()
        val om = Json.mapper()
        return om.writeValueAsString(openApi)
    }

    fun createContext(): KoaDslContext = KoaPluginDslContext(this)

    fun registerSchemaIfNotExists(name: String, schema: Schema<Any>) {
        storedSchemas.putIfAbsent(name, schema)
    }

    fun registerDescriptionHookForSubroutesOf(route: Route, descriptionBlock: DescriptionHook) {
        val hooksForRoute = hooks.getOrPut(route) { mutableListOf() }
        hooksForRoute += descriptionBlock
    }

    fun getHooksForRoute(route: Route): Sequence<DescriptionHook> {
        return sequence {
            route.traverseParents {
                yieldAll(hooks[it] ?: listOf())
            }
        }
    }
}

val Application.koa: Koa
    get() = feature(Koa)

private inline fun Route.traverseParents(elementHandler: (Route) -> Unit) {
    var current: Route? = this
    while (current != null) {
        elementHandler(current)
        current = current.parent
    }
}
