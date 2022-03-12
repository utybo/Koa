package guru.zoroark.koa.samples.ktor

import guru.zoroark.koa.dsl.schema
import guru.zoroark.koa.ktor.Koa
import guru.zoroark.koa.ktor.describe
import guru.zoroark.koa.ktor.respondOpenApiDocument
import guru.zoroark.koa.ktor.ui.KoaSwaggerUi
import guru.zoroark.koa.ktor.ui.swaggerUi
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.swagger.v3.oas.models.media.StringSchema

data class ServerState(var myString: String, var myNumber: Int)

fun main() {
    var state = ServerState("initial value", 1234)
    val server = embeddedServer(Netty, port = 8080) {
        install(Koa) {
            title = "Koa Ktor Sample"
            description = "Simple example of how you can use Koa in your Ktor application"
            version = "0.0.0"

            "read" tag {
                description = "Operations related to reading the data."
            }

            "write" tag {
                description = "Operations related to mutating the data."
            }
        }

        install(KoaSwaggerUi)

        install(ContentNegotiation) {
            jackson()
        }

        routing {
            route("/data") {
                get {
                    call.respond(state)
                } describe {
                    summary = "Get the data stored on this server."
                    description = "Returns the data stored on this server as a JSON object."
                    tags += "read"

                    200 response "application/json" {
                        description = "The state stored on the server."
                        schema(ServerState("Hello", 9999))
                    }
                }

                put {
                    state = call.receive()
                    call.respond(HttpStatusCode.NoContent)
                } describe {
                    summary = "Set the data stored on this server."
                    description = "Set the data stored on this server. No validation is done, which is not very safe to be honest."
                    tags += "write"

                    "application/json" requestBody {
                        description = "The new data that should be set on the server."
                        schema(ServerState("Hello", 9999))
                    }

                    204 response {
                        description = "Data on this server was set to the requested data"
                    }
                }

                patch("string") {
                    val newString = call.receive<String>()
                    state.myString = newString
                    call.respond(HttpStatusCode.NotFound)
                } describe {
                    summary = "Set the string of this server."
                    description = "Set the string stored in this server's data. No validation is done once again."
                    tags += "write"

                    "text/plain" requestBody {
                        description = "The string to set."
                        schema = StringSchema()
                        example = "Hello there"
                    }

                    204 response {
                        description = "It worked!"
                    }
                }
            }

            get("/openapi") {
                call.respondOpenApiDocument()
            }

            swaggerUi("/swagger", "/openapi")
        }
    }
    server.start(wait = true)
}