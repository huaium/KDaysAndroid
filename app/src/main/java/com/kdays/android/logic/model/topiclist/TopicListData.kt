package com.kdays.android.logic.model.topiclist

import com.google.gson.JsonElement

data class TopicListData(
    val allowOrderByList: List<String>,
    val allowOrderList: List<String>,
    val catList: JsonElement,
    val forum: Forum,
    val pages: Pages,
    val parameters: Parameters,
    val parentForums: List<Any>,
    val permission: Permission,
    val selectSubs: Any,
    val topicPageSize: Int,
    val topics: Topics
)