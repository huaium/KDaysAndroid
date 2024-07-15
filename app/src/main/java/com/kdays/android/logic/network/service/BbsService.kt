package com.kdays.android.logic.network.service

import com.google.gson.JsonObject
import com.kdays.android.logic.model.bbslogin.BbsLoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST

interface BbsService {
    @POST("api/plugin/call")
    fun bbsLogin(
        @Body body: JsonObject,
        @HeaderMap headers: Map<String, String>
    ): Call<BbsLoginResponse>
}