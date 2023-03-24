package nolambda.aichat.common.home.model

data class Message(
    val actor: Actor,
    val message: String,
) {
    enum class Actor {
        Person, Bot
    }
}
