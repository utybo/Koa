package guru.zoroark.koa.ktor

import guru.zoroark.koa.dsl.KoaDslContext
import io.swagger.v3.core.converter.AnnotatedType
import io.swagger.v3.core.converter.ModelConverters
import io.swagger.v3.core.jackson.ModelResolver
import io.swagger.v3.core.util.Json
import io.swagger.v3.oas.models.media.Schema

class KoaPluginDslContext(private val plugin: Koa) : KoaDslContext {
    override fun computeAndRegisterSchema(clazz: Class<*>): Schema<*> {
        val resolved = ModelConverters.getInstance().readAllAsResolvedSchema(clazz)
            ?: return ModelResolver(Json.mapper()).resolve(AnnotatedType().type(clazz), null, null)
        for ((name, schema) in resolved.referencedSchemas) {
            plugin.registerSchemaIfNotExists(name, schema)
        }
        return Schema<Any>().`$ref`(resolved.schema.name)
    }
}
