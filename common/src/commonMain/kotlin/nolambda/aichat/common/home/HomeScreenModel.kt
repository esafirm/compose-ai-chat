package nolambda.aichat.common.home

import cafe.adriel.voyager.core.model.StateScreenModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import nolambda.aichat.common.createSingleThreadDispatcher
import nolambda.aichat.common.data.http.OpenAiClient
import nolambda.aichat.common.data.model.AiModel
import nolambda.aichat.common.data.model.CompletionChunk
import nolambda.aichat.common.home.model.ChatElement
import nolambda.aichat.common.home.model.HomeScreenState
import nolambda.aichat.common.home.model.Message
import nolambda.aichat.common.home.model.asString

class HomeScreenModel(
    private val api: OpenAiClient = OpenAiClient(aiModel = AiModel.CHAT_GPT_3_5_TURBO),
    private val scope: CoroutineScope = CoroutineScope(createSingleThreadDispatcher()),
    private val elementGenerator: ChatElementGenerator = MarkdownChatElementGenerator(),
) : StateScreenModel<HomeScreenState>(HomeScreenState()) {

    fun ask(prompt: String) = scope.launch {
        val prevMessages = state.value.messages

        addPersonMessage(prompt)

        val flow = api.getCompletion(prompt, prevMessages)

        flow.collect {
            setStreamedMessage(it)
        }

        addBotMessage(state.value.streamedMessage)
    }

    private fun createChatElementsFromMessage(message: String): List<ChatElement> {
        return elementGenerator.generate(message)
    }

    private fun setStreamedMessage(newChunk: CompletionChunk) {
        setState {
            val rawMessage = streamedMessage.asString() + newChunk.choices.first().delta.content
            copy(
                streamedMessage = createChatElementsFromMessage(rawMessage)
            )
        }
    }

    private fun addPersonMessage(message: String) {
        setState {
            copy(
                messages = messages + Message(
                    actor = Message.Actor.Person,
                    text = message,
                    chatElements = createChatElementsFromMessage(message)
                )
            )
        }
    }

    private fun addBotMessage(chatElements: List<ChatElement>) {
        setState {
            copy(
                messages = messages + Message(
                    actor = Message.Actor.Bot,
                    text = chatElements.asString(),
                    chatElements = chatElements
                ),
                streamedMessage = emptyList(),
            )
        }
    }

    private fun setState(block: HomeScreenState.() -> HomeScreenState) {
        mutableState.value = state.value.block()
    }
}
