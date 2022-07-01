# Using Koa in Ktor

If you use Ktor, you can use `koa-ktor` to document and serve OpenAPI documentation about your application and `koa-ktor-ui` to serve Swagger UI. The two are independent: you can serve OpenAPI documentation without serving Swagger UI, and you can serve Swagger UI as long as you can grab the OpenAPI documentation from somewhere.

### Feature installation

Install the `Koa` feature in your `Ktor` application. The block passed to the `install` call describes the basic characteristics of your OpenAPI document.

```kotlin
install(Koa) {
    title = "My Koa application"
    version = "1.0.0"
}
```

See [the `Root DSL` documentation](DSL.md#root) to see all available options.

### Describing endpoints

Use the `describe` infix function to add descriptions to your endpoints.

```kotlin
// A plain-text endpoint
get("/hello") {
    call.respondText("Hello, world!")
} describe {
    summary = "Returns a greeting"
    200 response "text/plain" {
        example = "Hello, world!"
    }
}

// A more complicated JSON endpoint
data class Greeting(val greetingWord: String, val recipient: String)

get("/greeting/{recipient}") {
    call.respond(Greeting("Hello", call.parameters["recipient"]!!))
} describe {
    summary = "Returns a greeting"
    200 response "application/json" {
        schema<Greeting>()
        example = Greeting("Hello", "You")
        // Or a shorter alternative to do both on one line:
        // schema(Greeting("Hello", "World"))
    }
}
```

You can see all available options [in the `DescriptionDsl` reference](DSL.md#description).
