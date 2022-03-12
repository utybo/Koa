package guru.zoroark.koa.ktor

import guru.zoroark.koa.dsl.KoaDslContext
import io.swagger.v3.core.converter.ModelConverters
import io.swagger.v3.oas.models.media.Schema

class OpenApiPluginDslContext(private val plugin: Koa) : KoaDslContext {
    override fun computeAndRegisterSchema(clazz: Class<*>): Schema<*> {
        val resolved = ModelConverters.getInstance().readAllAsResolvedSchema(clazz)
        for ((name, schema) in resolved.referencedSchemas) {
            plugin.registerSchemaIfNotExists(name, schema)
        }
        return Schema<Any>().`$ref`(resolved.schema.name)
    }
}