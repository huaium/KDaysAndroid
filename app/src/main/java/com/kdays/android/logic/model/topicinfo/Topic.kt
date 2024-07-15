package com.kdays.android.logic.model.topicinfo

data class Topic(
    val cat: Int,
    val createAt: String,
    val flags: List<Any>,
    val hits: Int,
    val lastPoster: String,
    val locked: Int,
    val postAt: String,
    val rankNum: Int,
    val replies: Int,
    val special: Int,
    val subject: String,
    val tid: Int
)