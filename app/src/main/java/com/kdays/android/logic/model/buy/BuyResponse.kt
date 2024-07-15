package com.kdays.android.logic.model.buy

import com.google.gson.JsonElement

data class BuyResponse(
    val code: Int,
    val data: JsonElement?,
    val msg: String?
)