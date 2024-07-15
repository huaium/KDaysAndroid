package com.mohamedrejeb.richeditor.parser.bbcode

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import com.mohamedrejeb.richeditor.model.RichSpan
import com.mohamedrejeb.richeditor.model.RichSpanStyle
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.parser.RichTextStateParser
import com.mohamedrejeb.richeditor.utils.fastForEach
import com.mohamedrejeb.richeditor.utils.fastForEachIndexed

internal object RichTextStateBBCodeParser : RichTextStateParser<String> {
    override fun encode(input: String): RichTextState {
        throw NotImplementedError("no implementation")
    }

    override fun decode(richTextState: RichTextState): String {
        val builder = StringBuilder()

        richTextState.richParagraphList.fastForEachIndexed { _, richParagraph ->
            // Append paragraph start text
            builder.append(richParagraph.type.startRichSpan.text)

            // Append paragraph opening tag
            builder.append("<p>")

            // Append paragraph children
            richParagraph.children.fastForEach { richSpan ->
                builder.append(decodeRichSpanToBBCode(richSpan))
            }

            // Append paragraph closing tag
            builder.append("</p>")
        }

        return builder.toString()
    }

    private fun decodeRichSpanToBBCode(richSpan: RichSpan): String {
        val stringBuilder = StringBuilder()

        // Check if span is empty
        if (richSpan.isEmpty()) return ""

        // Append bbcode open
        stringBuilder.appendBBCodeOpen(richSpan)

        // Apply rich span style to bbcode
        val spanBBCode = decodeBBCodeElementFromRichSpan(
            richSpan.text,
            richSpan.style
        )

        // Append text
        stringBuilder.append(spanBBCode)

        // Append children
        richSpan.children.fastForEach { child ->
            stringBuilder.append(decodeBBCodeSpanToBBCode(child))
        }

        // Append bbcode close
        stringBuilder.appendBBCodeClose(richSpan)

        return stringBuilder.toString()
    }

    private fun decodeBBCodeElementFromRichSpan(
        text: String,
        richSpanStyle: RichSpanStyle,
    ): String {
        return when (richSpanStyle) {
            is RichSpanStyle.Link -> "[url=${richSpanStyle.url}]$text[/url]"
            is RichSpanStyle.Code -> "<pre class=\"code-brush\">$text</pre>"
            else -> text
        }
    }

    private fun decodeBBCodeSpanToBBCode(richSpan: RichSpan): String {
        val stringBuilder = StringBuilder()

        // Check if span is empty
        if (richSpan.isEmpty()) return ""

        // Append bbcode open
        stringBuilder.appendBBCodeOpen(richSpan)

        // Apply rich span style to bbcode
        val spanBBCode = decodeBBCodeElementFromRichSpan(
            richSpan.text,
            richSpan.style
        )

        // Append text
        stringBuilder.append(spanBBCode)

        // Append children
        richSpan.children.fastForEach { child ->
            stringBuilder.append(decodeRichSpanToBBCode(child))
        }

        // Append bbcode close
        stringBuilder.appendBBCodeClose(richSpan)

        return stringBuilder.toString()
    }

    private fun StringBuilder.appendBBCodeOpen(richSpan: RichSpan): StringBuilder {
        // Using string splice instead of StringBuilder,
        // because rarely will many styles be used
        var bbCodeOpen = ""

        when (richSpan.spanStyle.textDecoration) {
            TextDecoration.LineThrough -> bbCodeOpen += "[strike]"
            TextDecoration.Underline -> bbCodeOpen += "[u]"
        }

        when (richSpan.paragraph.paragraphStyle.textAlign) {
            TextAlign.Center -> bbCodeOpen += "[align=center]"
            TextAlign.Right -> bbCodeOpen += "[align=right]"
        }

        if ((richSpan.spanStyle.fontWeight?.weight ?: 400) > 400) bbCodeOpen += "[b]"
        if (richSpan.spanStyle.fontStyle == FontStyle.Italic) bbCodeOpen += "[i]"
        if (richSpan.spanStyle.color == Color.Red) bbCodeOpen += "[color=#ff0000]"
        if (richSpan.spanStyle.background == Color.Yellow) bbCodeOpen += "[backcolor=#ffff00]"

        return this.append(bbCodeOpen)
    }

    private fun StringBuilder.appendBBCodeClose(richSpan: RichSpan): StringBuilder {
        var bbCodeClose = ""

        when (richSpan.spanStyle.textDecoration) {
            TextDecoration.LineThrough -> bbCodeClose += "[/strike]"
            TextDecoration.Underline -> bbCodeClose += "[/u]"
        }

        if ((richSpan.spanStyle.fontWeight?.weight ?: 400) > 400) bbCodeClose += "[/b]"
        if (richSpan.spanStyle.fontStyle == FontStyle.Italic) bbCodeClose += "[/i]"
        if (richSpan.paragraph.paragraphStyle.textAlign != TextAlign.Left) bbCodeClose += "[/align]"
        if (richSpan.spanStyle.color != Color.Unspecified) bbCodeClose += "[/color]"
        if (richSpan.spanStyle.background != Color.Unspecified) bbCodeClose += "[/backcolor]"

        return this.append(bbCodeClose)
    }
}