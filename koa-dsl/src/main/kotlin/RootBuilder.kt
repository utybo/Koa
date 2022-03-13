package guru.zoroark.koa.dsl

import io.swagger.v3.oas.models.OpenAPI

class RootBuilder(
    private val infoBuilder: InfoBuilder = InfoBuilder()
) : RootDsl, InfoDsl by infoBuilder, Builder<OpenAPI> {
    private val tags = mutableListOf<TagBuilder>()
    override fun String.tag(tagBuilder: TagDsl.() -> Unit) {
        tags += TagBuilder(this).apply(tagBuilder)
    }

    override fun build(): OpenAPI = OpenAPI().apply {
        tags = this@RootBuilder.tags.map { it.build() }
        info = infoBuilder.build()
    }
}
