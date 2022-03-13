package guru.zoroark.koa.dsl

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
