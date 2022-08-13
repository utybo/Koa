# Heads up!

#### Koa has been superseded by [Tegral OpenAPI](https://tegral.zoroark.guru/docs/modules/core/openapi/). See [here](https://github.com/utybo/Koa/issues/9) for more information.

-----

**Original README**

### `koa` - Kotlin and Ktor OpenAPI

Koa intends to be a fully functional DSL for OpenAPI in Kotlin, including a Ktor plugin to add OpenAPI to your server Kotlin applications.

```kotlin
install(Koa) {
    title = "Koa example"
    description = "Ktor + OpenAPI (+ Swagger UI) = Koa!"
    version = "1.2.3"
}

install(KoaSwaggerUi) // Optional, you can remove it if you only want to serve the OpenAPI document

routing {
    get("/hello/there") {
        // do some complicated stuff...
    } describe {
        summary = "I am an endpoint!"
        description = "This is my endpoint. It's pretty nice, isn't it?"
        200 response "application/json" {
            description = "This is a successful response."
            schema(SomeObject(someParam = "some string"))
        }
    }
    
    // An endpoint to retrieve our OpenAPI document...
    get("/openapi") {
        call.respondOpenApiDocument()
    }
    
    // ... and an endpoint to serve Swagger UI. 
    // first parameter = path from which to serve
    // second parameter = path or URI from which OpenAPI document will be loaded
    swaggerUi("/swagger", "/openapi")
}
```

Experimental and very much a work in progress! Use at your own risk.

I do not currently have time to write full documentation for this, have a look at the sample (`samples/ktor`) for details, sorry :(

## Usage

You can add the libraries via [JitPack](https://jitpack.io/#guru.zoroark/koa).

```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'guru.zoroark:koa:main-SNAPSHOT'
    implementation 'guru.zoroark:koa-ktor:main-SNAPSHOT'
    implementation 'guru.zoroark:koa-ktor-ui:main-SNAPSHOT'
}
```

The following modules are present:

* `koa-dsl`, the DSL components of Koa without any dependencies other than Swagger Core
* `koa-ktor`, the Ktor feature which adds the ability to document endpoitns and serve OpenAPI documents
* `koa-ktor-ui`, utility feature for Ktor that allows you to serve Swagger UI easily. Does not actually depend on `koa-ktor`, you can also use it as-is as long as an OpenAPI document is served from somewhere.
* `samples`, sample applications
  * `samples:ktor` a more complete example of how you can use the `koa-ktor` feature.
