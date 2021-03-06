package guru.zoroark.koa.dsl

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI

interface RootDsl : InfoDsl, TagsDsl {
    infix fun String.securityScheme(scheme: SecuritySchemeDsl.() -> Unit)
}

class RootBuilder(
    private val infoBuilder: InfoBuilder = InfoBuilder(),
    private val securitySchemes: MutableMap<String, SecuritySchemeBuilder> = mutableMapOf()
) : RootDsl, InfoDsl by infoBuilder, Builder<OpenAPI> {
    private val tags = mutableListOf<TagBuilder>()
    override fun String.tag(tagBuilder: TagDsl.() -> Unit) {
        tags += TagBuilder(this).apply(tagBuilder)
    }

    override infix fun String.securityScheme(scheme: SecuritySchemeDsl.() -> Unit) {
        securitySchemes[this] = SecuritySchemeBuilder().apply(scheme)
    }

    override fun build(): OpenAPI = OpenAPI().apply {
        tags = this@RootBuilder.tags.map { it.build() }
        info = infoBuilder.build()
        if (securitySchemes.isNotEmpty()) {
            if (components == null) components = Components()
            securitySchemes.forEach { (key, securityScheme) ->
                components.addSecuritySchemes(key, securityScheme.build())
            }
        }
    }
}
