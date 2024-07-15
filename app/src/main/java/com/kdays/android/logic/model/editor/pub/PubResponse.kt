package com.kdays.android.logic.model.editor.pub

import com.google.gson.JsonElement

data class PubResponse(
    val code: Int,
    val data: JsonElement,
    val msg: String?
)