package nolambda.aichat.common.data.http

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.preparePost
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.appendIfNameAbsent
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import nolambda.aichat.BuildKonfig
import nolambda.aichat.common.data.model.CompletionChunk

class OpenAiClient(
    private val baseUrl: String = "https://api.openai.com/v1/",
    private val token: String = BuildKonfig.OPEN_AI_TOKEN,
) {

    companion object {
        private const val END_TOKEN = "[DONE]"
        private const val CHUNK_PREFIX = "data: "
    }

    private val httpClient = HttpClient {
        defaultRequest {
            url(baseUrl)
            header(HttpHeaders.Authorization, "Bearer $token")
            headers.appendIfNameAbsent(
                HttpHeaders.ContentType,
                ContentType.Application.Json.toString()
            )
        }
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                useAlternativeNames = false
                encodeDefaults = true
            })
        }
    }

    private fun createCompletionParameter(prompt: String): JsonObject {
        val messages = JsonArray(
            listOf(
                JsonObject(
                    mapOf(
                        "role" to JsonPrimitive("user"),
                        "content" to JsonPrimitive(prompt)
                    )
                )
            )
        )
        return JsonObject(
            mapOf(
                "model" to JsonPrimitive("gpt-3.5-turbo"),
                "stream" to JsonPrimitive(true),
                "max_tokens" to JsonPrimitive(1000),
                "messages" to messages,
            )
        )
    }

    suspend fun getCompletion(prompt: String): Flow<CompletionChunk> {
        val request = HttpRequestBuilder().apply {
            url("chat/completions")
            setBody(createCompletionParameter(prompt))
        }

        return flow {
            httpClient.preparePost(request).execute { httpResponse ->
                val channel: ByteReadChannel = httpResponse.body()

                while (!channel.isClosedForRead) {
                    val line = channel.readUTF8Line()
                    val data = line?.removePrefix(CHUNK_PREFIX)

                    if (!data.isNullOrBlank() && data != END_TOKEN) {
                        val chunk = Json.decodeFromString<CompletionChunk>(data)
                        emit(chunk)
                    }
                }
            }
        }
    }
}
