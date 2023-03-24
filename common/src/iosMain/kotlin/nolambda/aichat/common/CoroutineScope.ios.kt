package nolambda.aichat.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual fun createFixedDispatcher(executorCount: Int): CoroutineDispatcher {
    return Dispatchers.Main
}
