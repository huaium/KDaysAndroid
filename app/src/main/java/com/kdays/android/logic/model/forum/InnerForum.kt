package com.kdays.android.logic.model.forum

data class InnerForum(
    val about: String,
    val fid: String,
    val icon: String?,
    val lastPost: InnerForumPost?,
    val manager: List<String>,
    val name: String
)