package com.kdays.android.logic.model.uclogin

import com.google.gson.annotations.SerializedName

data class UcLoginData(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("app_state")
    val appState: String
)