package guru.zoroark.koa.dsl

import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.media.Schema

@KoaDsl
interface MediaTypeDsl {
    fun <T> schema(clazz: Class<T>)
    var schema: Schema<*>?
    var example: Any?
}

@KoaDsl
inline fun <reified T : Any> MediaTypeDsl.schema() = schema(T::class.java)

@KoaDsl
inline fun <reified T : Any> MediaTypeDsl.schema(example: T) {
    this.example = example
    schema(T::class.java)
}

@KoaDsl
fun <T> MediaTypeDsl.schema(clazz: Class<T>, example: T) {
    this.example = example
    schema(clazz)
}

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
