package guru.zoroark.koa.ktor.ui


import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.OutgoingContent
import io.ktor.http.defaultForFileExtension
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.AttributeKey
import io.ktor.util.InternalAPI
import java.util.Properties

private const val SWAGGER_UI_POM_LOCATION: String = "META-INF/maven/org.webjars/swagger-ui/pom.properties"

class KoaSwaggerUi(private val classpathPath: String) {
    class Configuration

    companion object Plugin : BaseApplicationPlugin<ApplicationCallPipeline, Configuration, KoaSwaggerUi> {
        override val key = AttributeKey<KoaSwaggerUi>("KoaSwaggerUi")

        private fun computeSwaggerUiPath(): String {
            val properties =
                KoaSwaggerUi::class.java.classLoader.getResourceAsStream(SWAGGER_UI_POM_LOCATION)
                    ?.use { Properties().apply { load(it) } }
                    ?: error("Failed to load, is org.webjars:swagger-ui on classpath?")
            val version = properties["version"]
            return "META-INF/resources/webjars/swagger-ui/$version"
        }

        override fun install(pipeline: ApplicationCallPipeline, configure: Configuration.() -> Unit): KoaSwaggerUi {
            val swaggerUiPath = computeSwaggerUiPath()
            return KoaSwaggerUi(swaggerUiPath)
        }
    }

    fun getPathFor(fileName: String) = "$classpathPath/$fileName"

    @OptIn(InternalAPI::class)
    fun getContentFor(fileName: String): OutgoingContent? {
        val path = getPathFor(fileName)
        return resourceClasspathResource(
            KoaSwaggerUi::class.java.classLoader.getResource(path)!!,
            path
        ) { ContentType.defaultForFileExtension(it) }
    }
}

fun Route.swaggerUi(path: String, openApiPath: String) {
    val indexContent by lazy {
        val originalIndex = KoaSwaggerUi::class.java.classLoader
            .getResourceAsStream(application.plugin(KoaSwaggerUi).getPathFor("index.html"))
            .use { it!!.bufferedReader().readText() }
        originalIndex.replace("url: \"https://petstore.swagger.io/v2/swagger.json\"", "url: \"$openApiPath\"")
    }
    get("$path/{fileName?}") {
        val fileName = call.parameters["fileName"] ?: "index.html"
        if (fileName == "index.html") {
            call.respondText(ContentType.Text.Html) { indexContent }
        } else {
            val result = application.plugin(KoaSwaggerUi).getContentFor(fileName)
            if (result != null) call.respond(result)
            else call.respond(HttpStatusCode.NotFound)
        }
    }
}
