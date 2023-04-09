package nolambda.aichat.common.data.model

data class AiModel(
    val modelName: String,
    val tokenLimit: Int,
    val maxTokenResult: Int,
) {
    companion object {
        val CHAT_GPT_3_5_TURBO = AiModel(
            modelName = "gpt-3.5-turbo",
            tokenLimit = 4000,
            maxTokenResult = 1000
        )
    }
}


