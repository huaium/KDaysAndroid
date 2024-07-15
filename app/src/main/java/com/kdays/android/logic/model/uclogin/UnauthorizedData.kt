package com.kdays.android.logic.model.uclogin

import com.google.gson.annotations.SerializedName

data class UnauthorizedData(@SerializedName("authorize_url") val authorizeUrl: String)
