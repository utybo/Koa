# Getting Started

Koa is made of the following modules:

- `koa-dsl`, a Kotlin DSL for creating OpenAPI models
- `koa-ktor`, a Ktor feature that lets you to document and serve OpenAPI documentation for Ktor apps.
- `koa-ktor-ui`, a Ktor feature that lets you serve Swagger UI right from your Ktor application.

## Adding Koa

!> Koa will be made available from sources other than JitPack in the future.

Koa is currently only available via JitPack. You can install it like so:

<!-- tabs:start -->

### **Gradle (Groovy)**

Put the following in your `build.gradle` file:

```groovy
repositories {
    mavenCentral()
    maven { url = 'https://jitpack.io' }
}

dependencies {
    // You can remove modules you do not need
    implementation 'guru.zoroark.koa:koa-dsl:VERSION'
    implementation 'guru.zoroark.koa:koa-ktor:VERSION'
    implementation 'guru.zoroark.koa:koa-ktor-ui:VERSION'
}
```

Replace `VERSION` with the version of Koa you want to use, a commit hash or a branch name followed by `-SNAPSHOT`. See [JitPack](https://jitpack.io/#guru.zoroark/koa) for more information.

### **Gradle (Kotlin)**

Put the following in your `build.gradle.kts` file:

```kotlin
repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    // You can remove modules you do not need
    implementation("guru.zoroark.koa:koa-dsl:VERSION")
    implementation("guru.zoroark.koa:koa-ktor:VERSION")
    implementation("guru.zoroark.koa:koa-ktor-ui:VERSION")
}
```

Replace `VERSION` with the version of Koa you want to use, a commit hash or a branch name followed by `-SNAPSHOT`. See [JitPack](https://jitpack.io/#guru.zoroark/koa) for more information.

### **Gradle (*.versions.toml)**

Add the following to your versions TOML file (e.g. `gradle/libs.versions.toml`):

```toml
[versions]
koa = "VERSION"

[dependencies]

# You can remove dependencies you do not need.

koa-dsl = { module = "guru.zoroark.koa:koa-dsl", version.ref = "koa" }
koa-ktor = { module = "guru.zoroark.koa:koa-ktor", version.ref = "koa" }
koa-ktor-ui = { module = "guru.zoroark.koa:koa-ktor-ui", version.ref = "koa" }

[bundles]
# If you remove dependencies, also remove them from here.
koa = ["koa-dsl", "koa-ktor", "koa-ktor-ui"]
```

Then, in your `build.gradle` file (Groovy DSL):

```groovy
repositories {
    mavenCentral()
    maven { url = 'https://jitpack.io' }
}

dependencies {
    implementation libs.bundles.koa
}
```

Or if you're using Kotlin DSL:

```kotlin
repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation(libs.bundles.koa)
}
```

?> You can read more about version catalogs [here](https://docs.gradle.org/current/userguide/platforms.html#sub:version-catalog.

<!-- tabs:end -->

## Modules

Koa provides the following modules:

- `koa-dsl`: a Kotlin DSL for creating OpenAPI models.

- `koa-ktor`: a Ktor plugin (fka feature) that lets you document your routes with OpenAPI using `koa-dsl`.
  - Depends on Ktor (`ktor-server-core`) and `koa-dsl`.

- `koa-ktor-ui`: a Ktor plugin (fka feature) that lets you serve Swagger UI right from your Ktor application.
  - Depends on Ktor (`ktor-server-core`).
  - Note that `koa-ktor-ui` can be used independently of `koa-ktor` and `koa-dsl` as long as you can server an OpenAPI document from somewhere.

