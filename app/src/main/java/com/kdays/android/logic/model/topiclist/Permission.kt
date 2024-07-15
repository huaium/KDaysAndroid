package com.kdays.android.logic.model.topiclist

import com.google.gson.annotations.SerializedName

data class Permission(
    val create: Boolean,
    @SerializedName("create_specials") val createSpecials: List<Int>,
    val manage: Boolean
)