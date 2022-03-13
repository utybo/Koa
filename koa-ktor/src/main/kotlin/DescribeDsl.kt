package guru.zoroark.koa.ktor

import guru.zoroark.koa.dsl.DescriptionBuilder
import guru.zoroark.koa.dsl.KoaDsl
import io.ktor.routing.Route
import io.ktor.routing.application
import io.ktor.util.pipeline.ContextDsl
import io.swagger.v3.oas.models.PathItem

@ContextDsl
@KoaDsl
infix fun Route.describe(descriptionBlock: DescriptionBuilder.() -> Unit) {
    val metadata = parseMetadataFromRoute(this)
    val operation = DescriptionBuilder(application.koa.createContext()).apply(descriptionBlock).build()
    application.koa.addOperation(
            "/" + metadata.httpPath.asReversed().joinToString("/"),
            PathItem.HttpMethod.valueOf(metadata.httpMethod!!),
            operation
    )
}
