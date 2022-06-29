package guru.zoroark.koa.ktor

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.response.*

suspend fun ApplicationCall.respondOpenApiDocument() {
    respondText(ContentType.Application.Json, HttpStatusCode.OK) {
        application.koa.buildOpenApiDocument()
    }
}
