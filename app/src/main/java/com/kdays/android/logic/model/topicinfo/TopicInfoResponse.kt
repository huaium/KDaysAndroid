package com.kdays.android.logic.model.topicinfo

data class TopicInfoResponse(
    val code: Int,
    val data: TopicInfoData,
    val msg: String?
)