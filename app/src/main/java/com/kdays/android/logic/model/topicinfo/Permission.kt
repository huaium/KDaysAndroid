package com.kdays.android.logic.model.topicinfo

import com.google.gson.annotations.SerializedName

data class Permission(
    val fav: Boolean,
    val manage: Boolean,
    val move: Boolean,
    val post: Boolean,
    @SerializedName("post_msg")
    val postMsg: Any,
    val rank: Boolean
)