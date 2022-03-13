package guru.zoroark.koa.dsl

import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.parameters.CookieParameter
import io.swagger.v3.oas.models.parameters.HeaderParameter
import io.swagger.v3.oas.models.parameters.Parameter
import io.swagger.v3.oas.models.parameters.PathParameter
import io.swagger.v3.oas.models.parameters.QueryParameter

enum class ParameterKind {
    Query,
    Header,
    Path,
    Cookie
}

@KoaDsl
class ParameterBuilder(
    private val context: KoaDslContext,
    val name: String,
    val kind: ParameterKind
) : Builder<Parameter>, MediaTypeDsl {
    var description: String? = null
    var required: Boolean? = null
    var deprecated: Boolean? = null
    var allowEmptyValue: Boolean? = null
    var style: Parameter.StyleEnum? = null
    var explode: Boolean? = null
    var allowReserved: Boolean? = null
    override var schema: Schema<*>? = null
    override var example: Any? = null

    // TODO content

    override fun <T> schema(clazz: Class<T>) {
        schema = context.computeAndRegisterSchema(clazz)
    }

    override fun build(): Parameter {
        val parameter = when (kind) {
            ParameterKind.Query -> QueryParameter()
            ParameterKind.Cookie -> CookieParameter()
            ParameterKind.Header -> HeaderParameter()
            ParameterKind.Path -> PathParameter()
        }

        parameter.name(name)
        description?.let { parameter.description(it) }
        required?.let { parameter.required(it) }
        deprecated?.let { parameter.deprecated(it) }
        allowEmptyValue?.let { parameter.allowEmptyValue(it) }
        style?.let { parameter.style(it) }
        explode?.let { parameter.explode(it) }
        allowReserved?.let { parameter.allowReserved(it) }
        schema?.let { parameter.schema(it) }
        example?.let { parameter.example(it) }

        return parameter
    }
}
