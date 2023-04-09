package nolambda.aichat.common.home.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import nolambda.aichat.common.LocalImage
import nolambda.aichat.common.LocalImages
import nolambda.aichat.common.home.model.ChatElement
import nolambda.aichat.common.home.model.Message

@Composable
internal fun ChatItemView(
    actor: Message.Actor,
    chatElements: List<ChatElement>,
    modifier: Modifier = Modifier,
) {

    Row(
        modifier = modifier.composed {
            if (actor == Message.Actor.Person) {
                Modifier.background(Color.LightGray.copy(0.5F))
                    .padding(16.dp)
                    .fillMaxWidth()
            } else {
                Modifier.padding(16.dp)
                    .fillMaxWidth()
            }
        }
    ) {

        val image = when (actor) {
            Message.Actor.Person -> LocalImages.Person
            Message.Actor.Bot -> LocalImages.Robot
        }

        LocalImage(
            imageResourceName = image,
            contentDescription = "Actor",
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            chatElements.forEachIndexed { index, el ->

                // Add spacing between each element
                if (index > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                when (el) {
                    is ChatElement.CodeBlock -> CodeBlockTextView(chatElement = el)
                    is ChatElement.Text -> SimpleMarkdownTextView(chatElement = el)
                }
            }
        }
    }
}

@Composable
internal fun CodeBlockTextView(chatElement: ChatElement.CodeBlock) {
    Text(
        text = chatElement.value,
        modifier = Modifier.fillMaxWidth()
            .background(Color.LightGray.copy(0.8F), shape = RoundedCornerShape(8.dp))
            .padding(16.dp),
        fontFamily = FontFamily.Monospace,
    )
}

@Composable
internal fun SimpleMarkdownTextView(chatElement: ChatElement.Text) {
    val annotatedString = remember(chatElement) {
        createAnnotatedString(chatElement.value)
    }

    Text(text = annotatedString)
}

private val backtickPattern = "`(.+?)`".toRegex()

private fun createAnnotatedString(text: String): AnnotatedString {
    val annotatedSpans = mutableListOf<AnnotatedString.Range<SpanStyle>>()
        .apply {
            val result = backtickPattern.findAll(text)
            result.forEach { match ->
                add(
                    AnnotatedString.Range(
                        SpanStyle(
                            fontWeight = FontWeight.SemiBold,
                            background = Color.LightGray.copy(0.5F),
                        ),
                        start = match.range.first,
                        end = match.range.last + 1
                    )
                )
            }
        }

    return buildAnnotatedString {
        append(text)
        annotatedSpans.forEach { range ->
            addStyle(
                style = range.item,
                start = range.start,
                end = range.end
            )
        }

    }
}
