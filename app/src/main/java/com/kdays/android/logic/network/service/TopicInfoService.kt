package com.kdays.android.logic.network.service

import com.google.gson.JsonObject
import com.kdays.android.logic.model.topicinfo.TopicInfoResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST

interface TopicInfoService {
    @POST("api/topic/info")
    fun getTopicInfo(
        @Body body: JsonObject,
        @HeaderMap headers: Map<String, String>
    ): Call<TopicInfoResponse>
}