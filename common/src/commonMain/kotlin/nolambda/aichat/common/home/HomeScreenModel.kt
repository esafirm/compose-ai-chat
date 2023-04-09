package nolambda.aichat.common.home

import cafe.adriel.voyager.core.model.StateScreenModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import nolambda.aichat.common.createSingleThreadDispatcher
import nolambda.aichat.common.data.http.OpenAiClient
import nolambda.aichat.common.data.model.AiModel
import nolambda.aichat.common.home.model.HomeScreenState
import nolambda.aichat.common.home.model.Message

class HomeScreenModel(
    private val api: OpenAiClient = OpenAiClient(aiModel = AiModel.CHAT_GPT_3_5_TURBO),
    private val scope: CoroutineScope = CoroutineScope(createSingleThreadDispatcher()),
) : StateScreenModel<HomeScreenState>(HomeScreenState()) {

    fun ask(prompt: String) = scope.launch {
        val prevMessages = state.value.messages

        addPersonMessage(prompt)

        val flow = api.getCompletion(prompt, prevMessages)

        flow.collect {
            mutableState.value = state.value.copy(
                streamedMessage = state.value.streamedMessage + it.choices.first().delta.content
            )
        }

        addBotMessage(state.value.streamedMessage)
    }

    private fun addPersonMessage(message: String) {
        setState {
            copy(
                messages = messages + Message(
                    actor = Message.Actor.Person,
                    text = message
                )
            )
        }
    }

    private fun addBotMessage(message: String) {
        setState {
            copy(
                messages = messages + Message(
                    actor = Message.Actor.Bot,
                    text = message
                ),
                streamedMessage = ""
            )
        }
    }

    private fun setState(block: HomeScreenState.() -> HomeScreenState) {
        mutableState.value = state.value.block()
    }
}
