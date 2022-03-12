package guru.zoroark.koa.dsl

import io.swagger.v3.oas.models.media.Schema

interface KoaDslContext {
    fun computeAndRegisterSchema(clazz: Class<*>): Schema<*>
}