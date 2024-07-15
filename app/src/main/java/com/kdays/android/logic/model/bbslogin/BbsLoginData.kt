package com.kdays.android.logic.model.bbslogin

import com.google.gson.annotations.SerializedName

data class BbsLoginData(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("token_expired")
    val tokenExpired: Long
)