package com.kdays.android.logic.network.service

import com.google.gson.JsonObject
import com.kdays.android.logic.model.topiclist.TopicListResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST

interface TopicListService {
    @POST("api/topic/list")
    fun getTopicList(
        @Body body: JsonObject,
        @HeaderMap headers: Map<String, String>
    ): Call<TopicListResponse>
}