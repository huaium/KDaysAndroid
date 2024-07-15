package com.kdays.android.logic.model.topiclist

import com.google.gson.annotations.SerializedName

data class Parameters(
    val cat: Int,
    val digest: Int,
    val fid: String,
    val sort: String,
    @SerializedName("sort_by") val sortBy: String,
    val uid: String
)