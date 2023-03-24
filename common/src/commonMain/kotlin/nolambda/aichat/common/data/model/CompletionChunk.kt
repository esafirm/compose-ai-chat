package nolambda.aichat.common.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CompletionChunk(
    @SerialName("choices")
    val choices: List<Choice>,
    @SerialName("created")
    val created: Int,
    @SerialName("id")
    val id: String,
    @SerialName("model")
    val model: String,
    @SerialName("object")
    val objectX: String,
) {

    @Serializable
    data class Choice(
        @SerialName("delta")
        val delta: Delta,
        @SerialName("finish_reason")
        val finishReason: String? = null,
        @SerialName("index")
        val index: Int,
    ) {
        @Serializable
        data class Delta(
            @SerialName("content")
            val content: String = "",
            @SerialName("role")
            val role: String = "",
        )
    }
}
