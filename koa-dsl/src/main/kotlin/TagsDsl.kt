package guru.zoroark.koa.dsl

interface TagsDsl {
    infix fun String.tag(tagBuilder: TagDsl.() -> Unit)
}
