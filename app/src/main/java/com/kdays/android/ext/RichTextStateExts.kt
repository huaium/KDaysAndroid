package com.kdays.android.ext

import com.mohamedrejeb.richeditor.model.RichTextState

object RichTextStateExts {
    fun RichTextState.addEmotion(
        id: String
    ) {
        if (id.isEmpty()) return

        val emotionText = "[s:$id]"

        addText(emotionText)
    }

    fun RichTextState.addSell(
        point: String,
        text: String,
        id: String
    ) {
        if (text.isEmpty()) return

        val sellText = "[sell=$point,$id]$text[/sell]"

        addText(sellText)
    }

    fun RichTextState.addHide(
        point: String,
        text: String,
        id: String
    ) {
        if (text.isEmpty()) return

        val hideText = "[hide=$point,$id]$text[/hide]"

        addText(hideText)
    }

    fun RichTextState.addImage(
        url: String
    ) {
        if (url.isEmpty()) return

        val imageText = "[img]$url[/img]"

        addText(imageText)
    }
}