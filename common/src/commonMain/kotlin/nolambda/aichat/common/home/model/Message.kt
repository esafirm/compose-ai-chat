package nolambda.aichat.common.home.model

data class Message(
    val actor: Actor,
    val text: String,
) {
    enum class Actor {
        Person, Bot
    }
}
