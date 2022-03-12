package guru.zoroark.koa.ktor

import io.ktor.routing.*
import java.util.*

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
        else -> {/* TODO avoid ignoring silently */}
    }
    parseMutableMetadataFromSelector(route.parent, metadata)
}

fun parseMetadataFromRoute(route: Route): EndpointMetadata {
    val res = MutableEndpointMetadata().apply { parseMutableMetadataFromSelector(route, this) }
    return EndpointMetadata(
            httpMethod = res.httpMethod,
            httpPath = res.httpPath
    )
}