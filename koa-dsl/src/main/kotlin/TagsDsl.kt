package guru.zoroark.koa.dsl

interface TagsDsl {
    infix fun String.tag(tagBuilder: TagDsl.() -> Unit)
}

interface TagDsl {
    var description: String?
    var externalDocsDescription: String?
    var externalDocsUrl: String?
}