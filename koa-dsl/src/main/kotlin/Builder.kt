package guru.zoroark.koa.dsl

fun interface Builder<out T> {
    fun build(): T
}
