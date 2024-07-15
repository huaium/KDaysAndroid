package com.kdays.android.logic.model.uclogin

import com.google.gson.JsonElement

data class UcLoginResponse(
    val code: Int,
    val data: JsonElement,
    val msg: String?
)