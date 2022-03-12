package guru.zoroark.koa.ktor.ui

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import java.io.BufferedReader
import java.util.*

class KoaSwaggerUi(private val classpathPath: String, config: Configuration) {
    class Configuration

    companion object Feature : ApplicationFeature<ApplicationCallPipeline, Configuration, KoaSwaggerUi> {
        override val key = AttributeKey<KoaSwaggerUi>("KoaSwaggerUi")

        private fun computeSwaggerUiPath(): String {
            val properties = KoaSwaggerUi::class.java.classLoader.getResourceAsStream("META-INF/maven/org.webjars/swagger-ui/pom.properties")
                    ?.use { Properties().apply { load(it) } }
                    ?: error("Failed to load, is org.webjars:swagger-ui on classpath?")
            val version = properties["version"]
            return "META-INF/resources/webjars/swagger-ui/$version"
        }

        override fun install(pipeline: ApplicationCallPipeline, configure: Configuration.() -> Unit): KoaSwaggerUi {
            val swaggerUiPath = computeSwaggerUiPath()
            return KoaSwaggerUi(swaggerUiPath, Configuration().apply(configure))
        }
    }

    fun getPathFor(fileName: String) = "$classpathPath/$fileName"

    @OptIn(InternalAPI::class)
    fun getContentFor(fileName: String): OutgoingContent? {
        val path = getPathFor(fileName)
        return resourceClasspathResource(KoaSwaggerUi::class.java.classLoader.getResource(path)!!, path) { ContentType.defaultForFileExtension(it) }
    }
}

fun Route.swaggerUi(path: String, openApiPath: String) {
    val indexContent by lazy {
        val originalIndex = KoaSwaggerUi::class.java.classLoader
                .getResourceAsStream(application.feature(KoaSwaggerUi).getPathFor("index.html"))
                .use { it!!.bufferedReader().readText() }
        originalIndex.replace("url: \"https://petstore.swagger.io/v2/swagger.json\"", "url: \"$openApiPath\"")
    }
    get("$path/{fileName?}") {
        val fileName = call.parameters["fileName"] ?: "index.html"
        if (fileName == "index.html") {
            call.respondText(ContentType.Text.Html) { indexContent }
        } else {
            val result = application.feature(KoaSwaggerUi).getContentFor(fileName)
            if (result == null)
                call.respond(HttpStatusCode.NotFound)
            else
                call.respond(result)
        }
    }
}