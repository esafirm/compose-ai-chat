package nolambda.aichat.common

import kotlinx.coroutines.CoroutineDispatcher

/**
 * Create a fixed thread pool dispatcher with the given number of threads.
 */
expect fun createFixedDispatcher(executorCount: Int): CoroutineDispatcher

fun createSingleThreadDispatcher() = createFixedDispatcher(1)
