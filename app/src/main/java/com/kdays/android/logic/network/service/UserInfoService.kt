package com.kdays.android.logic.network.service

import com.google.gson.JsonObject
import com.kdays.android.logic.model.userinfo.UserInfoResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST

interface UserInfoService {
    @POST("api/my/info")
    fun getUserInfo(
        @Body body: JsonObject,
        @HeaderMap headers: Map<String, String>
    ): Call<UserInfoResponse>
}