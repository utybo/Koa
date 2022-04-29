package guru.zoroark.koa.ktor

import guru.zoroark.koa.dsl.OperationBuilder
import guru.zoroark.koa.dsl.KoaDsl
import io.ktor.routing.HttpMethodRouteSelector
import io.ktor.routing.Route
import io.ktor.routing.application
import io.ktor.util.pipeline.ContextDsl
import io.swagger.v3.oas.models.PathItem

@ContextDsl
@KoaDsl
infix fun Route.describe(descriptionBlock: OperationBuilder.() -> Unit) {
    val metadata = parseMetadataFromRoute(this)
    val operation = OperationBuilder(application.koa.createContext()).apply(descriptionBlock).build()

    if (metadata.httpMethod == null) {
        // Workaround for https://github.com/utybo/Koa/issues/5 | KTOR-4239
        if (this.children.isNotEmpty() && this.children.last().selector is HttpMethodRouteSelector) {
            metadata.httpMethod = (this.children.last().selector as HttpMethodRouteSelector).method.value
        }
    }

    val finalMetadata = metadata.freeze()
    application.koa.addOperation(
            "/" + metadata.httpPath.asReversed().joinToString("/"),
            PathItem.HttpMethod.valueOf(finalMetadata.httpMethod!!),
            operation
    )
}
