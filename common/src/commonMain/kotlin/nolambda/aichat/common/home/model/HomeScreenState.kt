package nolambda.aichat.common.home.model

import cafe.adriel.voyager.core.model.ScreenModel

data class HomeScreenState(
    val messages: List<Message> = emptyList(),
    val streamedMessage: String = "",
) : ScreenModel
