package nolambda.aichat.common.home.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import nolambda.aichat.common.home.model.Message

@Composable
internal fun ChatView(
    messages: List<Message> = emptyList(),
    streamedMessage: String = "",
    onSendMessage: (message: String) -> Unit,
) {
    val (message, setMessage) = remember { mutableStateOf("") }

    Column {
        LazyColumn(
            modifier = Modifier.weight(1F)
        ) {
            items(messages) { message ->
                ChatItemView(
                    actor = message.actor,
                    text = message.text
                )
            }
            if (streamedMessage.isNotBlank()) {
                item {
                    ChatItemView(
                        actor = Message.Actor.Bot,
                        text = streamedMessage
                    )
                }
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextField(
                value = message,
                onValueChange = setMessage,
                placeholder = { Text("Enter your message") },
                modifier = Modifier.weight(1F)
            )
            IconButton(onClick = {
                onSendMessage(message)
                setMessage("")
            }) {
                Icon(imageVector = Icons.Default.Send, contentDescription = "Send")
            }
        }
    }
}
