package nolambda.aichat.common.home.model

import cafe.adriel.voyager.core.model.ScreenModel

data class HomeScreenState(
    val messages: List<Message> = emptyList(),
    val streamedMessage: List<ChatElement> = emptyList(),
) : ScreenModel

sealed class ChatElement(
    open val value: String,
) {
    data class Text(
        override val value: String,
    ) : ChatElement(value)

    data class CodeBlock(
        override val value: String,
        val language: String,
    ) : ChatElement(value)
}

fun List<ChatElement>.asString(): String {
    return joinToString(separator = "\n") { it.value }
}
