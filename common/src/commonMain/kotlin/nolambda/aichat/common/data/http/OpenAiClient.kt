package nolambda.aichat.common.data.http

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
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
import nolambda.aichat.common.data.model.AiModel
import nolambda.aichat.common.data.model.CompletionChunk
import nolambda.aichat.common.home.model.Message

class OpenAiClient(
    private val baseUrl: String = "https://api.openai.com/v1/",
    private val token: String = BuildKonfig.OPEN_AI_TOKEN,
    private val tokenCounter: TokenCounter = SpaceTokenCounter(),
    private val aiModel: AiModel,
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
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.BODY
        }
    }

    private fun createMessageParam(message: Message): JsonObject {
        val role = when (message.actor) {
            Message.Actor.Person -> "user"
            Message.Actor.Bot -> "assistant"
        }

        return JsonObject(
            mapOf(
                "role" to JsonPrimitive(role),
                "content" to JsonPrimitive(message.text)
            )
        )
    }

    private fun createCompletionParameter(prompt: String, prevMessages: List<Message>): JsonObject {
        val newPrompt = JsonObject(
            mapOf(
                "role" to JsonPrimitive("user"),
                "content" to JsonPrimitive(prompt)
            )
        )

        // Set up the context and make sure it doesn't exceed the token limit
        var tokenCount = 0
        val messageContent = mutableListOf<JsonObject>()

        for (msg in prevMessages.asReversed()) {
            val addition = tokenCounter.countToken(msg.text)
            if (tokenCount + addition + aiModel.maxTokenResult > aiModel.tokenLimit) {
                break
            }
            tokenCount += addition
            messageContent.add(createMessageParam(msg))
        }

        // Add a new prompt
        messageContent.add(newPrompt)

        return JsonObject(
            mapOf(
                "model" to JsonPrimitive(aiModel.modelName),
                "stream" to JsonPrimitive(true),
                "max_tokens" to JsonPrimitive(aiModel.maxTokenResult),
                "messages" to JsonArray(messageContent),
            )
        )
    }

    suspend fun getCompletion(
        prompt: String,
        prevMessages: List<Message> = emptyList(),
    ): Flow<CompletionChunk> {
        val request = HttpRequestBuilder().apply {
            url("chat/completions")
            setBody(createCompletionParameter(prompt, prevMessages))
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

    interface TokenCounter {
        fun countToken(text: String): Int
    }

    class SpaceTokenCounter : TokenCounter {
        override fun countToken(text: String): Int {
            return text.count { it == ' ' } + 1
        }
    }
}
