package com.kdays.android.logic.model.editor.emotion

data class EmotionResponse(
    val code: Int,
    val data: EmotionData,
    val msg: String?
)