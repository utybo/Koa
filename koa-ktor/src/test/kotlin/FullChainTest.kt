package guru.zoroark.koa.ktor

import guru.zoroark.koa.dsl.schema
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.testing.withTestApplication
import kotlin.test.Test
import kotlin.test.assertEquals

data class ExampleDataClass(val a: String, val b: Int)

class FullChainTest {
    data class C(val text: String)
    data class A(val c: C)
    data class B(val c: C)

    @Test
    fun `Full test`() {
        withTestApplication({
            install(Koa) {
                title = "My example API"
                version = "0.0.0"
            }

            routing {
                get("/test/plain") {
                    call.respond("Hello there!")
                } describe {
                    summary = "I am a greeter"
                    200 response {
                        description = "Greets you."
                        "text/plain" content {
                            example = "Hello there!"
                        }
                    }
                }

                get("/test/json") {
                    call.respond(ExampleDataClass("test", 101))
                } describe {
                    summary = "Sends you JSON"
                    200 response {
                        "application/json" content {
                            schema<ExampleDataClass>()
                            example = ExampleDataClass("one", 1)
                        }
                    }
                }

                application.koa.buildOpenApiDocument()
            }
        }) {}
    }

    @Test
    fun `Full test, custom type used twice`() {
        withTestApplication({
            install(Koa) {
                title = "My example API"
                version = "0.0.0"
            }

            routing {
                get("/test/a") {
                    call.respond(A(C("hello from a")))
                } describe {
                    200 response "application/json" {
                        description = "This is A."
                        schema<A>()
                        example = A(C("example"))
                    }
                }

                get("/test/b") {
                    call.respond(B(C("hello from b")))
                } describe {
                    200 response "application/json" {
                        description = "This is B."
                        schema<B>()
                        example = B(C("example"))
                    }
                }

                get("/test/c/my{someParameter}") {
                    call.respond(call.parameters["someParameter"] ?: "null")
                } describe {
                    "someParameter" pathParameter {
                        description = "a parameter"
                        schema<String>("bruh")
                    }
                    200 response "text/string" {
                        description = "the parameter from the request"
                    }
                }

                application.koa.buildOpenApiDocument()
            }
        }) {}
    }

    @Test
    fun `Test with describeSubroutes`() {
        withTestApplication({
            install(Koa) {
                title = "My example API"
                version = "0.0.0"
                "foo" tag {
                    description = "A tag"
                }
            }

            routing {
                route("/foo") {
                    describeSubroutes {
                        tags += "foo"
                    }

                    get("/bar") {
                        call.respondText("bar")
                    } describe {
                        description = "yes"
                    }

                    routeWithDescription({
                        tags += "one"
                    }) {
                        get("one") {
                            call.respondText("one")
                        } describe {
                            description = "one"
                        }
                    }

                    get("/baz") {
                        call.respondText("baz")
                    } describe {
                        description = "yes"
                    }
                }

                get("/unrelated") {
                    call.respondText("unrelated")
                } describe {
                    description = "bruh"
                }
            }
        }) {
            val openApi = application.koa.buildOpenApiObject()
            assertEquals(4, openApi.paths.size)
            assertEquals(3, openApi.paths.count { it.value.get.tags.contains("foo") })
            assertEquals(1, openApi.paths.count { it.value.get.tags.contains("one") })
        }
    }
}
