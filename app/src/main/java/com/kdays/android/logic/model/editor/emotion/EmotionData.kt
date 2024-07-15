package com.kdays.android.logic.model.editor.emotion

import com.google.gson.annotations.SerializedName

data class EmotionData(
    @SerializedName("data") val categories: List<Category>,
    val baseDir: String
)