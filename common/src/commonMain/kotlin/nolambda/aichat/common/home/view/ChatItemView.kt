package nolambda.aichat.common.home.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import nolambda.aichat.common.LocalImage
import nolambda.aichat.common.LocalImages
import nolambda.aichat.common.home.model.Message
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.parser.MarkdownParser

@Composable
internal fun ChatItemView(
    actor: Message.Actor,
    text: String,
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

        val annotatedString = remember(text) {

            val flavour = CommonMarkFlavourDescriptor()
            val parsedTree = MarkdownParser(flavour).buildMarkdownTreeFromString(text)

            fun printChildren(depth: Int, node: ASTNode) {
                node.children.forEach {
                    println(" ".repeat(depth) + node.type.toString() + " -> " + it.getTextInNode(text))
                    printChildren(depth + 1, it)
                }
            }
            printChildren(0, parsedTree)

            createAnnotatedString(text)
        }

        Text(text = annotatedString)
    }
}

val backtickPattern = "`(.+?)`".toRegex()

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
