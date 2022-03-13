package guru.zoroark.koa.dsl

import io.swagger.v3.oas.models.ExternalDocumentation
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.parameters.Parameter
import io.swagger.v3.oas.models.responses.ApiResponse
import io.swagger.v3.oas.models.responses.ApiResponses

@KoaDsl
class DescriptionBuilder(private val context: KoaDslContext) : Builder<Operation> {
    var summary: String? = null
    val responses = mutableMapOf<Int, Builder<ApiResponse>>()
    val tags = mutableListOf<String>()
    var description: String? = null
    var externalDocsDescription: String? = null
    var externalDocsUrl: String? = null
    var requestBody: RequestBodyBuilder? = null
    val parameters = mutableListOf<Builder<Parameter>>()

    // TODO operationId, callbacks, security, servers
    var deprecated: Boolean = false

    @KoaDsl
    infix fun Int.response(builder: ResponseBuilder.() -> Unit) {
        responses[this] = ResponseBuilder(context).apply(builder)
    }

    @KoaDsl
    infix fun Int.response(stub: BodyStub) {
        responses[this] = Builder {
            ResponseBuilder(context).apply {
                // Transfer response information from stub
                description = stub.description
                contentTypes[stub.contentType] = stub.mediaTypeBuilder
            }.build()
        }
    }

    @KoaDsl
    infix fun String.requestBody(builder: BodyStub.() -> Unit) {
        val stub = BodyStub(this, context).apply(builder)
        requestBody = RequestBodyBuilder(context).apply {
            description = stub.description
            contentTypes[stub.contentType] = stub.mediaTypeBuilder
        }
    }

    @KoaDsl
    operator fun String.invoke(builder: BodyStub.() -> Unit): BodyStub =
        BodyStub(this, context).apply(builder)

    @KoaDsl
    infix fun String.pathParameter(builder: ParameterBuilder.() -> Unit) {
        parameters += ParameterBuilder(context, this, ParameterKind.Path).apply(builder)
    }

    @KoaDsl
    infix fun String.headerParameter(builder: ParameterBuilder.() -> Unit) {
        parameters += ParameterBuilder(context, this, ParameterKind.Header).apply(builder)
    }

    @KoaDsl
    infix fun String.cookieParameter(builder: ParameterBuilder.() -> Unit) {
        parameters += ParameterBuilder(context, this, ParameterKind.Cookie).apply(builder)
    }

    @KoaDsl
    infix fun String.queryParameter(builder: ParameterBuilder.() -> Unit) {
        parameters += ParameterBuilder(context, this, ParameterKind.Query).apply(builder)
    }

    override fun build(): Operation = Operation().apply {
        summary = this@DescriptionBuilder.summary
        responses(ApiResponses().apply {
            for ((returnCode, responseBuilder) in this@DescriptionBuilder.responses) {
                addApiResponse(returnCode.toString(), responseBuilder.build())
            }
        })
        tags = this@DescriptionBuilder.tags
        description = this@DescriptionBuilder.description
        if (externalDocsUrl != null || externalDocsDescription != null) {
            externalDocs = ExternalDocumentation().apply {
                description = externalDocsDescription
                url = externalDocsUrl
            }
        }
        deprecated = this@DescriptionBuilder.deprecated
        requestBody = this@DescriptionBuilder.requestBody?.build()
        parameters = this@DescriptionBuilder.parameters.map { it.build() }
    }
}
