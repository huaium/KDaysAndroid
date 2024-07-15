package com.kdays.android.logic.model.bbslogin

import com.google.gson.JsonElement

data class BbsLoginResponse(
    val code: Int,
    val msg: String?,
    val data: JsonElement
)