package guru.zoroark.koa.dsl

import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.media.Schema

class MediaTypeBuilder(private val context: KoaDslContext) : MediaTypeDsl, Builder<MediaType> {
    override var schema: Schema<*>? = null
    override var example: Any? = null

    override fun build(): MediaType = MediaType().apply {
        example = this@MediaTypeBuilder.example
        schema = this@MediaTypeBuilder.schema
    }

    override fun <T> schema(clazz: Class<T>) {
        context.computeAndRegisterSchema(clazz).also { schema = it }
    }
}
