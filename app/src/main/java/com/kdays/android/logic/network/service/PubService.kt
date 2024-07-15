package com.kdays.android.logic.network.service

import com.google.gson.JsonObject
import com.kdays.android.logic.model.editor.pub.PubResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST

interface PubService {
    @POST("api/pub/topic")
    fun pub(
        @Body body: JsonObject,
        @HeaderMap headers: Map<String, String>,
    ): Call<PubResponse>
}