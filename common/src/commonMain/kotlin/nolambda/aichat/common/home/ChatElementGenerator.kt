package nolambda.aichat.common.home

import nolambda.aichat.common.home.model.ChatElement
import nolambda.aichat.common.markdown.Markdown
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode
import org.intellij.markdown.parser.MarkdownParser

interface ChatElementGenerator {
    fun generate(text: String): List<ChatElement>
}

class MarkdownChatElementGenerator(
    private val parser: Lazy<MarkdownParser> = lazy { Markdown.parser },
) : ChatElementGenerator {

    companion object {
        private const val CODE_FENCE_MARK = "```"
    }

    override fun generate(text: String): List<ChatElement> {
        val astNode = parser.value.buildMarkdownTreeFromString(text)
        return iterateAstNode(astNode, text)
    }

    private fun iterateAstNode(node: ASTNode, text: String): List<ChatElement> {
        val elements = mutableListOf<ChatElement>()

        fun visitNode(node: ASTNode) {
            val codeBlockElement = node.tryCreateCodeBlockElement(text)

            if (codeBlockElement != null) {
                elements.add(codeBlockElement)
                return
            }

            val isParagraph = node.type == MarkdownElementTypes.PARAGRAPH
            if (isParagraph) {
                elements.add(
                    ChatElement.Text(
                        value = node.getTextInNode(text).toString()
                    )
                )
                return
            }

            node.children.forEach { visitNode(it) }
        }
        visitNode(node)

        return elements
    }

    private fun ASTNode.tryCreateCodeBlockElement(text: String): ChatElement? {
        if (children.isEmpty()) return null

        val isCodeFence = type == MarkdownElementTypes.CODE_FENCE
        if (!isCodeFence) return null

        val textInNode = getTextInNode(text).toString()
        val lines = textInNode.lines()
        val language = lines.first().trim().removePrefix(CODE_FENCE_MARK)

        val lastIndex = if (lines.lastOrNull()?.contains(CODE_FENCE_MARK) == true) {
            lines.lastIndex
        } else {
            lines.lastIndex + 1
        }

        return ChatElement.CodeBlock(
            value = lines.subList(1, lastIndex).joinToString(separator = "\n"),
            language = language
        )
    }
}
