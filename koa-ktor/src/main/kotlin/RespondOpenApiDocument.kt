package guru.zoroark.koa.ktor

import io.ktor.application.ApplicationCall
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondText

suspend fun ApplicationCall.respondOpenApiDocument() {
    respondText(ContentType.Application.Json, HttpStatusCode.OK) {
        application.koa.buildOpenApiDocument()
    }
}
