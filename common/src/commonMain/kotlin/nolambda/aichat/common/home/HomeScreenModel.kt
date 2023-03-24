package nolambda.aichat.common.home

import cafe.adriel.voyager.core.model.StateScreenModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import nolambda.aichat.common.createSingleThreadDispatcher
import nolambda.aichat.common.data.http.OpenAiClient
import nolambda.aichat.common.home.model.HomeScreenState
import nolambda.aichat.common.home.model.Message

class HomeScreenModel(
    private val api: OpenAiClient = OpenAiClient(),
    private val scope: CoroutineScope = CoroutineScope(createSingleThreadDispatcher()),
) : StateScreenModel<HomeScreenState>(HomeScreenState()) {

    fun ask(prompt: String) = scope.launch {
        addPersonMessage(prompt)

        val flow = api.getCompletion(prompt)

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
                    message = message
                )
            )
        }
    }

    private fun addBotMessage(message: String) {
        setState {
            copy(
                messages = messages + Message(
                    actor = Message.Actor.Bot,
                    message = message
                ),
                streamedMessage = ""
            )
        }
    }

    private fun setState(block: HomeScreenState.() -> HomeScreenState) {
        mutableState.value = state.value.block()
    }
}
