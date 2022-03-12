package guru.zoroark.koa.ktor

import guru.zoroark.koa.dsl.*
import io.ktor.application.*
import io.ktor.util.*
import io.swagger.v3.core.util.Json
import io.swagger.v3.oas.models.*
import io.swagger.v3.oas.models.media.Schema

class Koa(config: Configuration) {
    private val pathItems = mutableMapOf<String, PathItem>()
    private val openApiBaseBuilder: RootBuilder = config.builder
    private val storedSchemas = mutableMapOf<String, Schema<*>>()

    class Configuration(internal val builder: RootBuilder = RootBuilder()) : RootDsl by builder

    companion object Feature : ApplicationFeature<ApplicationCallPipeline, Configuration, Koa> {
        override val key = AttributeKey<Koa>("Koa")

        override fun install(pipeline: ApplicationCallPipeline, configure: Configuration.() -> Unit): Koa =
                Koa(Configuration().apply(configure))
    }

    fun addOperation(path: String, method: PathItem.HttpMethod, operation: Operation) {
        pathItems.getOrPut(path) { PathItem() }.operation(method, operation)
    }

    fun makeOpenApiDocument(): String {
        // TODO most of this could be made a one-time lazy operation?
        val openApi = openApiBaseBuilder.build().apply {
            paths = Paths().apply {
                pathItems.forEach { (k, v) -> addPathItem(k, v) }
            }
            components = Components().apply {
                for ((schemaName, schema) in storedSchemas)
                    addSchemas(schemaName, schema)
            }
        }
        val om = Json.mapper()
        val result = om.writeValueAsString(openApi)
        return result
    }

    fun createContext(): KoaDslContext = OpenApiPluginDslContext(this)

    fun registerSchemaIfNotExists(name: String, schema: Schema<Any>) {
        storedSchemas.putIfAbsent(name, schema)
    }
}

val Application.koa: Koa
    get() = feature(Koa)