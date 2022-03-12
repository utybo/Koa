package guru.zoroark.koa.dsl

@KoaDsl
class BodyStub private constructor(
        var contentType: String,
        internal val mediaTypeBuilder: MediaTypeBuilder
) : PartialBodyDsl, MediaTypeDsl by mediaTypeBuilder {
    constructor(contentType: String, context: KoaDslContext) : this(contentType, MediaTypeBuilder(context))

    override var description: String? = null
}