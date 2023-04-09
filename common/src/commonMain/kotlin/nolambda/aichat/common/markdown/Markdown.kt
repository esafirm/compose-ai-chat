package nolambda.aichat.common.markdown

import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.parser.MarkdownParser

object Markdown {

    val parser by lazy {
        val flavour = CommonMarkFlavourDescriptor()
        MarkdownParser(flavour)
    }
}
