package guru.zoroark.koa.ktor

import guru.zoroark.koa.dsl.KoaDsl
import guru.zoroark.koa.dsl.OperationBuilder
import io.ktor.server.routing.HttpMethodRouteSelector
import io.ktor.server.routing.Route
import io.ktor.server.routing.RouteSelector
import io.ktor.server.routing.RouteSelectorEvaluation
import io.ktor.server.routing.RoutingResolveContext
import io.ktor.server.routing.application
import io.ktor.util.KtorDsl
import io.swagger.v3.oas.models.PathItem

@KtorDsl
@KoaDsl
infix fun Route.describe(descriptionBlock: OperationBuilder.() -> Unit) {
    val metadata = parseMetadataFromRoute(this)
    val descriptionWithHooks: OperationBuilder.() -> Unit = {
        application.koa.getHooksForRoute(this@describe).forEach { with(it) { applyHook() } }
        descriptionBlock()
    }
    val operation = OperationBuilder(application.koa.createContext()).apply(descriptionWithHooks).build()

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

@KtorDsl
@KoaDsl
fun Route.describeSubroutes(descriptionBlock: DescriptionHook) {
    application.koa.registerDescriptionHookForSubroutesOf(this, descriptionBlock)
}

@KtorDsl
fun Route.routeWithDescription(descriptionHook: DescriptionHook, routeBuilder: Route.() -> Unit): Route {
    val subroute = createChild(object : RouteSelector() {
        override fun evaluate(context: RoutingResolveContext, segmentIndex: Int) = RouteSelectorEvaluation.Transparent
    })
    with(subroute) {
        describeSubroutes(descriptionHook)
        routeBuilder()
    }
    return subroute
}
