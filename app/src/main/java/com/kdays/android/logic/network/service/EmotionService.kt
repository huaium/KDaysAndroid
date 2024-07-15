package com.kdays.android.logic.network.service

import com.kdays.android.logic.model.editor.emotion.EmotionResponse
import retrofit2.Call
import retrofit2.http.GET

interface EmotionService {
    @GET("pub/getEmotion")
    fun getEmotion(): Call<EmotionResponse>
}