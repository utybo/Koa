package guru.zoroark.koa.dsl

import io.swagger.v3.oas.models.security.SecurityScheme

interface RootDsl : InfoDsl, TagsDsl {
    infix fun String.securityScheme(scheme: SecurityScheme)
}
