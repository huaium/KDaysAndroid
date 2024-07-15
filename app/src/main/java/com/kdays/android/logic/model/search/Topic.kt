package com.kdays.android.logic.model.search

data class Topic(
    val author: Author,
    val cat: Int,
    val createAt: String,
    val flags: List<Any>,
    val hits: Int,
    val icon: String,
    val lastPoster: String,
    val locked: Int,
    val postAt: String,
    val rankNum: Int,
    val replies: Int,
    val special: Int,
    val style: Any?,
    val subject: String,
    val tid: Int
)