package com.kdays.android.logic.network.service

import com.google.gson.JsonObject
import com.kdays.android.logic.model.editor.reply.ReplyResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST

interface ReplyService {
    @POST("api/pub/reply")
    fun reply(
        @Body body: JsonObject,
        @HeaderMap headers: Map<String, String>,
    ): Call<ReplyResponse>
}