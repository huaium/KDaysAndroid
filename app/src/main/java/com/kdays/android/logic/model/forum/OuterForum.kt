package com.kdays.android.logic.model.forum

data class OuterForum(
    val about: String,
    val fid: String,
    val forums: List<InnerForum>,
    val icon: String?,
    val manager: List<String>,
    val name: String
)