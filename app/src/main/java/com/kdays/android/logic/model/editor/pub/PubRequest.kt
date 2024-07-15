package com.kdays.android.logic.model.editor.pub

data class PubRequest(
    val fid: String,
    val selectedCat: String,
    val subject: String,
    val content: String,
    val token: String
)