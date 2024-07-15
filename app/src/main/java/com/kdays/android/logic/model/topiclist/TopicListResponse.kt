package com.kdays.android.logic.model.topiclist

data class TopicListResponse(
    val code: Int,
    val data: TopicListData?,
    val msg: String?
)