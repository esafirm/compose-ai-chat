package nolambda.aichat.desktop

import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.runBlocking
import nolambda.aichat.common.data.http.OpenAiClient
import nolambda.aichat.common.data.model.AiModel

fun main() {
    runBlocking {
        val flow = OpenAiClient(
            aiModel = AiModel.CHAT_GPT_3_5_TURBO
        ).getCompletion("Please write an essay about the future of AI.")

        flow.onCompletion {
            println("COMPLETE!")
        }

        flow.collect {
            println(it)
        }

        println("After flow")
    }
}
