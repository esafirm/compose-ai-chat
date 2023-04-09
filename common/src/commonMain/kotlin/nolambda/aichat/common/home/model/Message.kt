package nolambda.aichat.common.home.model

data class Message(
    val actor: Actor,
    val text: String,
    val chatElements: List<ChatElement>
) {
    enum class Actor {
        Person, Bot
    }
}
