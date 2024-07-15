package com.kdays.android.logic.model.usertopics

data class UserTopicsData(
    val allowOrderByList: List<String>,
    val allowOrderList: List<String>,
    val pages: Pages,
    val parameters: Parameters,
    val topicPageSize: Int,
    val topics: Topics
)