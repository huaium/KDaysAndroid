package com.kdays.android.logic.model.topiclist

data class Topic(
    val author: Author,
    val cat: Int,
    val createAt: String,
    val flags: List<Any>,
    val forum: Forum?,
    val hits: Int,
    val lastPoster: String,
    val locked: Int,
    val postAt: String,
    val rankNum: Int,
    val replies: String,
    val special: Int,
    val style: Style?,
    val subject: String,
    val tid: String
)