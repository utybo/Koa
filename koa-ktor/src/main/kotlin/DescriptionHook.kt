package guru.zoroark.koa.ktor

import guru.zoroark.koa.dsl.OperationDsl

fun interface DescriptionHook {
    fun OperationDsl.applyHook()
}
