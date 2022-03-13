package guru.zoroark.koa.dsl

import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.parameters.RequestBody

class RequestBodyBuilder(context: KoaDslContext) : BodyBuilder(context), Builder<RequestBody> {
    override fun build(): RequestBody = RequestBody().apply {
        description = this@RequestBodyBuilder.description
        content = Content().apply {
            for ((typeString, typeBuilder) in contentTypes) {
                addMediaType(typeString, typeBuilder.build())
            }
        }
    }
}
