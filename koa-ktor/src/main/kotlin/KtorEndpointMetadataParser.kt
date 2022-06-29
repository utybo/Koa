package guru.zoroark.koa.ktor


import io.ktor.server.routing.*
import java.util.LinkedList

data class EndpointMetadata(
    val httpMethod: String? = null,
    val httpPath: List<String> = listOf()
)

data class MutableEndpointMetadata(
    var httpMethod: String? = null,
    val httpPath: MutableList<String> = LinkedList()
) {
    fun freeze() = EndpointMetadata(httpMethod, httpPath.toList())
}

tailrec fun parseMutableMetadataFromSelector(route: Route?, metadata: MutableEndpointMetadata) {
    if (route == null) return

    val selector = route.selector

    when (selector) {
        is HttpMethodRouteSelector -> metadata.httpMethod = selector.method.value
        is PathSegmentConstantRouteSelector -> metadata.httpPath += selector.value
        is PathSegmentParameterRouteSelector -> metadata.httpPath += selector.prefix.orEmpty() + "{${selector.name}}" +
                selector.suffix.orEmpty()
        else -> {
            /* TODO avoid ignoring silently */
        }
    }
    parseMutableMetadataFromSelector(route.parent, metadata)
}

fun parseMetadataFromRoute(route: Route): MutableEndpointMetadata {
    return MutableEndpointMetadata().apply { parseMutableMetadataFromSelector(route, this) }
}
