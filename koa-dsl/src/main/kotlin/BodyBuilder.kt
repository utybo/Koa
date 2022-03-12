package guru.zoroark.koa.dsl

import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.responses.ApiResponse

@KoaDsl
abstract class BodyBuilder(protected val context: KoaDslContext) : PartialBodyDsl {
    override var description: String? = null
    val contentTypes = mutableMapOf<String, Builder<MediaType>>()

    @KoaDsl
    infix fun String.content(builder: MediaTypeBuilder.() -> Unit) {
        contentTypes[this] = MediaTypeBuilder(context).apply(builder)
    }
}

@KoaDsl
class ResponseBuilder(context: KoaDslContext) : BodyBuilder(context), Builder<ApiResponse> {
    override fun build(): ApiResponse = ApiResponse().apply {
        description(this@ResponseBuilder.description)
        content = Content().apply {
            for ((typeString, typeBuilder) in contentTypes) {
                addMediaType(typeString, typeBuilder.build())
            }
        }
    }
}