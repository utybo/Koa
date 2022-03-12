package guru.zoroark.koa.ktor

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*

suspend fun ApplicationCall.respondOpenApiDocument() {
    respondText(ContentType.Application.Json, HttpStatusCode.OK) {
        application.koa.makeOpenApiDocument()
    }
}