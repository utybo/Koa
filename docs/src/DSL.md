# DSL Reference

!> Koa's DSL is not complete yet, please [open an issue](https://github.com/utybo/Koa/issues) if you find something missing (or better yet, send a pull request!).

## Introduction

Koa is based on **OpenAPI 3.0.1** and its DSL produces models from the [Swagger Core](https://github.com/swagger-api/swagger-core) family of libraries. As such, it follows the objects described in the [OpenAPI specification](https://spec.openapis.org/oas/v3.0.1.html).

Koa's DSL model is simple: you manipulate Koa's builders (which include various methods for mutating the model in a Kotlin-friendly way), which then produce Swagger Core models upon being built. These built models can then be consumed by others (e.g. `koa-ktor`).

### Naming scheme

There are two kinds of types in the DSL:

#### DSL interfaces

Named `XyzDsl`, these are interfaces that are used when a specific set of DSL functions can be applied to multiple builders *or* to objects that are not builders at all[^1]. An example is `koa-ktor`'s feature configuration object, which is a `KoaDsl` while not being a builder at all and deferring all DSL-ish operations to an actual `KoaBuilder` instance.

The most common example is that of an example + schema combo, which can be found in response builders, parameter builders, etc. A single `XyzDsl` type may correspond to multiple builders. If you wish to extend Koa's DSL in some way, you should create extension functions onto the `XyzDsl` interfaces instead of the builders if possible.

?> Ultimately, all builder classes should only contain implementations of functions and properties declared in a DSL interface, but we're not there yet.

#### Builder classes

Named `XyzBuilder`, they are the classes that are in charge of building a model from Swagger Core. These actually contain the building logic and state, as well as the implementation for relevant `XyzDsl` interfaces.

### DSLs and scopes

Each DSL component contains one or more "available DSLs". This means that you can use all the functions of the mentioned DSLs when you are within the scope of this component (e.g. for a Response builder, you are in a `response {...}` block). These available DSLs may impact the builder itself and the object it builds (*direct*) **or** may have an impact on an object that is deeper in the built model hierarchy (*indirect*). The latter is mostly used for convenience.

!> There is not necessarily a one-to-one mapping between DSL interfaces and builder classes: it may be that a DSL interface is only used indirectly. This is usually the case if it is extremely easy to build and a builder is not deemed necessary.

## Components

### Root

* DSL interface: `RootDsl`
* Builder class: `RootBuilder`
* Available DSLs: `RootDsl` (direct), [`InfoDsl`](#info) (indirect), [`TagsDsl`](#tags) (indirect)
* Output: `OpenAPI (io.swagger.v3.oas.models.OpenAPI)`

The **Root DSL** is the root of the DSL model. It is a wrapper for the `OpenAPI` object and contains fundamental fields and functionality that are used for root-level fields of the OpenAPI object, such as `tags` or `info`.

By itself, the root DSL is only a combination of multiple DSL interfaces -- it does not provide any DSL of its own. See each individual interface to learn more.

#### Example

```kotlin
// These come from InfoDsl
title = "My great API"
version = "1.0.0"

contactName = "Smith & Co"
contactEmail = "api@smithandco.example.com"
contactUrl = "https://smithandco.example.com/contact"

licenseName = "Smith & Co Developer License"
licenseUrl = "https://smithandco.example.com/license"

// This comes from TagsDsl
"some-tag" tag {
    description = "Endpoints made for some kind of tag"
}
```

### Info

* DSL interface: `InfoDsl`
* Builder class: `InfoBuilder`
* Available DSLs: `InfoDsl` (direct)
* Output: `Info (io.swagger.v3.oas.models.info.Info)` + all sub-objects

Represents information about the API document.

Note that this DSL is usually accessed via the [root DSL](#root) and not by itself.

#### Properties

* Related to [Information](https://spec.openapis.org/oas/v3.0.1.html#fixed-fields-0)
  * `title: String`
  * `description: String`
  * `termsOfService: String`
  * `version: String`
* Related to [Contact](https://spec.openapis.org/oas/v3.0.1.html#fixed-fields-1)
  * `contactName: String`
  * `contactUrl: String`
  * `contactEmail: String`
* Related to [License](https://spec.openapis.org/oas/v3.0.1.html#fixed-fields-2)
  * `licenseName: String`
  * `licenseUrl: String`

### Tags

* DSL interface: `TagsDsl`
* Available DSLs: `TagsDsl` (direct)

A DSL for the definition of tags for this document. Not callable by itself. Use the [root DSL](#root) to make use of this DSL.

#### Functions

* `String tag { TagDsl }` Adds a tag to the document. You can use the [tag DSL](#tag) to further describe the tag.

#### Example

```kotlin
"my_tag" tag {
    description = "This is my tag"
}
```

### Tag

* DSL interface: `TagDsl`
* Builder class: `TagBuilder`
* Available DSLs: `TagDsl` (direct)
* Output: `Tag (io.swagger.v3.oas.models.tags.Tag)` + all sub-objects

Provides information about a tag that will be used in the document.

#### Properties

* `description: String`
* `externalDocsDescription: String`
* `externalDocsUrl: String`

#### Example

```kotlin
description = "My tag"
externalDocsDescription = "My external docs"
externalDocsUrl = "https://example.com"
```

### Description

* DSL interface: (none)
* Builder class: `DescriptionBuilder`
* Available DSLs: (none other than own)
* Output: `Description (io.swagger.v3.oas.models.Operation)` + external docs

Describes the content and behavior of an API endpoint. The meta-information about the endpoint (its path and its HTTP method) is automatically determined when using Ktor. Also known as an "operation" in OpenAPI jargon.

#### Properties

* Simple properties
  * `summary: String`
  * `description: String`
  * `externalDocsDescription: String`
  * `externalDocsUrl: String`
  * `tags = mutableListOf<String>()`. Use `tags += "my_tag"` to add a tag.
* Complex properties. These properties should not be manipulated directly -- consider using the relevant functions instead.
  * `requestBody`
  * `responses`
  * `parameters`

#### Functions

| Prototype | Description |
|:---------:|-------------|
| `Int response { ResponseDsl }` | Adds a response with the given status code that is initialized via the given response builder (see [the response DSL](#response) for details). |
| `Int response String { BodyDsl }` | Adds a response with the given status code that is initialized via the given body builder (see [the body DSL](#body) for details). |
| `String requestBody { BodyDsl }` | Sets the request body for this operation with the given content type that is initialized via the given body builder (see [the body DSL](#body) for details). |
| `String pathParameter { ParameterDsl }` | Adds a path parameter to the operation. You can use the [parameter DSL](#parameter) to further describe the parameter. |
| `String headerParameter { ParameterDsl }` | Adds a header parameter to the operation. You can use the [parameter DSL](#parameter) to further describe the parameter. |
| `String queryParameter { ParameterDsl }` | Adds a query parameter to the operation. You can use the [parameter DSL](#parameter) to further describe the parameter. |
| `String cookieParameter { ParameterDsl }` | Adds a cookie parameter to the operation. You can use the [parameter DSL](#parameter) to further describe the parameter. |

#### Example

```kotlin
summary = "This is an operation."
description = "This is an operation. It does this and that. It's pretty nice."

tags += "my_tag"

"X-My-Header" headerParameter {
    description = "My header's description"
    schema("MyHeaderValue")
}

"application/json" requestBody { 
    description = "Some request body"
    schema(SomeObject("Hello", 9999)) // see the body DSL for more information
}

200 response "application/json" {
    description = "Data was changed and mutated data was returned"
    schema(SomeObject("Hello", 9999)) // see the body DSL for more information
}

400 response {
    description = "The sent data was invalid"

    "application/json" content {
        schema(SomeError("Invalid data", 1))
    }

    "text/plain" content {
        schema("Invalid data")
    }
}
```

### Body

* DSL interface: `PartialBodyDsl`
* Builder class: Subclasses of `BodyBuilder` (`RequestBodyBuilder`, `ResponseBuilder`)
* Available DSLs: `PartialBodyDsl` (direct)

Represents a body, which usually is just a combination of a description, and content types. This DSL is used both for response and request bodies.

#### Properties

* Simple properties
  * `description: String`
* Complex properties
  * `contentTypes`

#### Functions

| Prototype | Description |
|:---------:|-------------|
| `String content { MediaTypeDsl }` | Adds a [media type](#media-type) to this body builder. The given string is the media type in question (e.g. `application/json`) |

#### Example

```kotlin
description = "Somebody once told me..."

"application/json" content {
    schema(SomeObject("Hello", 9999))
}
```

### Media Type

* DSL interface:  `MediaTypeDsl`
* Builder class: `MediaTypeBuilder`
* Available DSLs: `MediaTypeDsl` (direct)
* Output: `Description (io.swagger.v3.oas.models.Operation)` + external docs

#### Properties

* `schema: Schema<*>`. This allows you to specify "simple" schemas from OpenAPI. If you wish to use Kotlin/Java classes as schemas, consider using `schema<T>()` or `schema(T::class)` instead (see below).

* `example: Any?`, an example of the value. Should match the schema specified in `schema`. You can also declare an example with the `schema(...)` functions, see below.

Represents a schema and an example value for said schema.

#### Functions

| Prototype | Description |
|:---------:|-------------|
| `schema<T>()` | Computes a schema for the given type and sets this media type to said schema. |
| `schema<T>(example)` | Same as above, but also sets the example to the given value. If the example is exactly of the same type as `T`, you can remove `<T>` (your IDE should suggest it). |
| `schema(T::class.java)` | Computes a schema for the given `Class` and sets this media type to said schema. |
| `schema(T::class.java, example)` | Same as above, but also sets the example to the given value. |

Note that:

- Schemas computed using these functions are cached and reused if the same type is used across multiple endpoints. They will result in a `$ref` in the final OpenAPI schema.

- Generic versions of the `schema` functions are inlined and use reified type. You cannot use them if the type is not known at compile-time -- use the non-reified version (with a class reference) instead.

```kotlin
// All of the following are equivalent:

schema<SomeObject>()
example = SomeObject("Hello", 9999)

schema(SomeObject("Hello", 9999))

schema(SomeObject::class.java)
example = SomeObject("Hello", 9999)

schema(SomeObject::class.java, SomeObject("Hello", 9999))
```