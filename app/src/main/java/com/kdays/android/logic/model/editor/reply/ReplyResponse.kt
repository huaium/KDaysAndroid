package com.kdays.android.logic.model.editor.reply

import com.google.gson.JsonElement

data class ReplyResponse(
    val code: Int,
    val data: JsonElement,
    val msg: String?
)