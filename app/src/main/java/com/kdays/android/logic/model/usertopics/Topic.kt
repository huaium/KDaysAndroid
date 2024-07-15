package com.kdays.android.logic.model.usertopics

import com.kdays.android.logic.model.topiclist.Style

data class Topic(
    val cat: Int,
    val createAt: String,
    val flags: List<String>,
    val forum: Forum,
    val hits: Int,
    val lastPoster: String,
    val locked: Int,
    val postAt: String,
    val rankNum: Int,
    val replies: Int,
    val special: Int,
    val style: Style?,
    val subject: String,
    val tid: String
)