package com.kdays.android.logic.model.topicinfo

data class Post(
    val attachments: List<Any>,
    val author: Author,
    val content: String,
    val date: String,
    val flags: List<Any>,
    val floor: String,
    val permission: Permission,
    val pid: String,
    val rankRecords: List<Any>,
    val subject: String?
)